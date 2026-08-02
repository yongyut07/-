const contractABI = [
  {
    "inputs": [
      {
        "internalType": "string[]",
        "name": "candidateNames",
        "type": "string[]"
      }
    ],
    "stateMutability": "nonpayable",
    "type": "constructor"
  },
  {
    "inputs": [],
    "name": "candidateCount",
    "outputs": [
      {
        "internalType": "uint256",
        "name": "",
        "type": "uint256"
      }
    ],
    "stateMutability": "view",
    "type": "function"
  },
  {
    "inputs": [
      {
        "internalType": "uint256",
        "name": "",
        "type": "uint256"
      }
    ],
    "name": "candidateList",
    "outputs": [
      {
        "internalType": "string",
        "name": "",
        "type": "string"
      }
    ],
    "stateMutability": "view",
    "type": "function"
  },
  {
    "inputs": [],
    "name": "endVote",
    "outputs": [],
    "stateMutability": "nonpayable",
    "type": "function"
  },
  {
    "inputs": [],
    "name": "startVote",
    "outputs": [],
    "stateMutability": "nonpayable",
    "type": "function"
  },
  {
    "inputs": [],
    "name": "state",
    "outputs": [
      {
        "internalType": "enum Voting.State",
        "name": "",
        "type": "uint8"
      }
    ],
    "stateMutability": "view",
    "type": "function"
  },
  {
    "inputs": [
      {
        "internalType": "string",
        "name": "candidate",
        "type": "string"
      }
    ],
    "name": "totalVotesFor",
    "outputs": [
      {
        "internalType": "uint256",
        "name": "",
        "type": "uint256"
      }
    ],
    "stateMutability": "view",
    "type": "function"
  },
  {
    "inputs": [
      {
        "internalType": "string",
        "name": "candidate",
        "type": "string"
      }
    ],
    "name": "voteForCandidate",
    "outputs": [],
    "stateMutability": "nonpayable",
    "type": "function"
  }
];

const stateNames = ["Created", "Voting", "Ended"];

let provider = null;
let signer = null;
let contract = null;
let currentAccount = null;
let currentState = null;
let candidates = [];

const connectButton = document.getElementById("connectButton");
const loadContractButton = document.getElementById("loadContractButton");
const startVoteButton = document.getElementById("startVoteButton");
const endVoteButton = document.getElementById("endVoteButton");
const refreshButton = document.getElementById("refreshButton");
const contractAddressInput = document.getElementById("contractAddress");
const walletAddressElement = document.getElementById("walletAddress");
const networkNameElement = document.getElementById("networkName");
const voteStateElement = document.getElementById("voteState");
const accountRoleElement = document.getElementById("accountRole");
const candidateListElement = document.getElementById("candidateList");
const candidateTotalElement = document.getElementById("candidateTotal");
const messageBox = document.getElementById("messageBox");

function showMessage(message, type = "info") {
  messageBox.className = `message ${type}`;
  messageBox.textContent = message;
}

function shortAddress(address) {
  if (!address) return "-";
  return `${address.slice(0, 6)}...${address.slice(-4)}`;
}

function getErrorMessage(error) {
  if (error?.code === 4001 || error?.code === "ACTION_REJECTED") {
    return "ผู้ใช้ยกเลิกคำสั่งใน MetaMask";
  }

  return (
    error?.reason ||
    error?.shortMessage ||
    error?.info?.error?.message ||
    error?.message ||
    "เกิดข้อผิดพลาดที่ไม่ทราบสาเหตุ"
  );
}

async function connectWallet() {
  try {
    if (!window.ethereum) {
      throw new Error("ไม่พบ MetaMask กรุณาติดตั้ง MetaMask ก่อนใช้งาน");
    }

    provider = new ethers.BrowserProvider(window.ethereum);
    await provider.send("eth_requestAccounts", []);
    signer = await provider.getSigner();
    currentAccount = await signer.getAddress();

    const network = await provider.getNetwork();

    walletAddressElement.textContent = shortAddress(currentAccount);
    walletAddressElement.title = currentAccount;
    networkNameElement.textContent = `${network.name} (Chain ID: ${network.chainId})`;
    connectButton.textContent = "เชื่อมต่อแล้ว";

    showMessage("เชื่อมต่อ MetaMask สำเร็จ", "success");

    if (contractAddressInput.value.trim()) {
      await loadContract();
    }
  } catch (error) {
    console.error(error);
    showMessage(getErrorMessage(error), "error");
  }
}

async function loadContract() {
  try {
    if (!signer) {
      await connectWallet();
      if (!signer) return;
    }

    const address = contractAddressInput.value.trim();

    if (!ethers.isAddress(address)) {
      throw new Error("Contract Address ไม่ถูกต้อง");
    }

    const bytecode = await provider.getCode(address);
    if (bytecode === "0x") {
      throw new Error("ไม่พบ Smart Contract ที่ Address นี้บนเครือข่ายปัจจุบัน");
    }

    contract = new ethers.Contract(address, contractABI, signer);
    localStorage.setItem("votingContractAddress", address);

    startVoteButton.disabled = false;
    endVoteButton.disabled = false;
    refreshButton.disabled = false;

    showMessage("กำลังโหลดข้อมูลจาก Smart Contract...", "pending");
    await refreshData();
    showMessage("โหลด Smart Contract สำเร็จ", "success");
  } catch (error) {
    console.error(error);
    contract = null;
    showMessage(getErrorMessage(error), "error");
  }
}

async function refreshData() {
  if (!contract) return;

  try {
    const stateValue = await contract.state();
    currentState = Number(stateValue);

    voteStateElement.textContent = stateNames[currentState] || "Unknown";
    accountRoleElement.textContent =
      currentState === 0
        ? "บัญชีผู้ Deploy เท่านั้นที่เริ่มโหวตได้"
        : "ผู้ใช้งานระบบ";

    startVoteButton.disabled = currentState !== 0;
    endVoteButton.disabled = currentState !== 1;

    const count = Number(await contract.candidateCount());
    candidates = [];

    for (let index = 0; index < count; index++) {
      const candidateName = await contract.candidateList(index);
      candidates.push(candidateName);
    }

    candidateTotalElement.textContent = `${count} คน`;
    await renderCandidates();
  } catch (error) {
    console.error(error);
    showMessage(getErrorMessage(error), "error");
  }
}

async function renderCandidates() {
  candidateListElement.innerHTML = "";

  if (candidates.length === 0) {
    candidateListElement.innerHTML =
      '<div class="empty-state">ไม่พบรายชื่อผู้สมัครใน Smart Contract</div>';
    return;
  }

  for (let index = 0; index < candidates.length; index++) {
    const candidate = candidates[index];
    const card = document.createElement("article");
    card.className = "candidate-card";

    let voteText = "คะแนนจะแสดงเมื่อจบการโหวต";

    if (currentState === 2) {
      try {
        const votes = await contract.totalVotesFor(candidate);
        voteText = `ได้รับ ${votes.toString()} คะแนน`;
      } catch {
        voteText = "ไม่สามารถอ่านคะแนนได้";
      }
    }

    const number = document.createElement("span");
    number.className = "candidate-number";
    number.textContent = String(index + 1);

    const title = document.createElement("h3");
    title.textContent = candidate;

    const voteCount = document.createElement("p");
    voteCount.className = "vote-count";
    voteCount.textContent = voteText;

    const voteButton = document.createElement("button");
    voteButton.className = "button primary";
    voteButton.textContent = "ลงคะแนน";
    voteButton.disabled = currentState !== 1;
    voteButton.addEventListener("click", () => vote(candidate));

    card.append(number, title, voteCount, voteButton);
    candidateListElement.appendChild(card);
  }
}

async function vote(candidate) {
  try {
    if (!contract) {
      throw new Error("กรุณาโหลด Smart Contract ก่อน");
    }

    showMessage(`กรุณายืนยันการโหวต "${candidate}" ใน MetaMask`, "pending");

    const transaction = await contract.voteForCandidate(candidate);
    showMessage(`ส่ง Transaction แล้ว: ${transaction.hash}`, "pending");

    await transaction.wait();

    showMessage(`ลงคะแนนให้ "${candidate}" สำเร็จ`, "success");
    await refreshData();
  } catch (error) {
    console.error(error);
    showMessage(getErrorMessage(error), "error");
  }
}

async function startVote() {
  try {
    showMessage("กรุณายืนยันการเริ่มโหวตใน MetaMask", "pending");
    const transaction = await contract.startVote();
    showMessage(`ส่ง Transaction แล้ว: ${transaction.hash}`, "pending");
    await transaction.wait();

    showMessage("เริ่มการโหวตสำเร็จ", "success");
    await refreshData();
  } catch (error) {
    console.error(error);
    showMessage(getErrorMessage(error), "error");
  }
}

async function endVote() {
  try {
    showMessage("กรุณายืนยันการจบโหวตใน MetaMask", "pending");
    const transaction = await contract.endVote();
    showMessage(`ส่ง Transaction แล้ว: ${transaction.hash}`, "pending");
    await transaction.wait();

    showMessage("จบการโหวตสำเร็จ สามารถดูคะแนนได้แล้ว", "success");
    await refreshData();
  } catch (error) {
    console.error(error);
    showMessage(getErrorMessage(error), "error");
  }
}

connectButton.addEventListener("click", connectWallet);
loadContractButton.addEventListener("click", loadContract);
startVoteButton.addEventListener("click", startVote);
endVoteButton.addEventListener("click", endVote);
refreshButton.addEventListener("click", refreshData);

window.addEventListener("load", () => {
  const savedAddress = localStorage.getItem("votingContractAddress");
  if (savedAddress) {
    contractAddressInput.value = savedAddress;
  }
});

if (window.ethereum) {
  window.ethereum.on("accountsChanged", () => {
    window.location.reload();
  });

  window.ethereum.on("chainChanged", () => {
    window.location.reload();
  });
}
