// SPDX-License-Identifier: UNLICENSED
pragma solidity ^0.8.17;

contract Voting {
    address public officialAddress;
    string[] public candidateList;
    mapping(string => uint256) private votesReceived;
    mapping(address => bool) public isVoted;
    mapping(string => bool) public isCandidate;

    enum State {
        Created,
        Voting,
        Ended
    }

    State public state;

    constructor(string[] memory candidateNames) {
        require(candidateNames.length > 0, "Candidates required");

        officialAddress = msg.sender;
        state = State.Created;

        for (uint256 i = 0; i < candidateNames.length; i++) {
            require(bytes(candidateNames[i]).length > 0, "Invalid candidate");
            require(!isCandidate[candidateNames[i]], "Duplicate candidate");

            candidateList.push(candidateNames[i]);
            isCandidate[candidateNames[i]] = true;
        }
    }

    modifier onlyOfficial() {
        require(msg.sender == officialAddress, "Only Official");
        _;
    }

    modifier inState(State requiredState) {
        require(state == requiredState, "Invalid voting state");
        _;
    }

    function startVote()
        external
        onlyOfficial
        inState(State.Created)
    {
        state = State.Voting;
    }

    function endVote()
        external
        onlyOfficial
        inState(State.Voting)
    {
        state = State.Ended;
    }

    function candidateCount() external view returns (uint256) {
        return candidateList.length;
    }

    function voteForCandidate(string calldata candidate)
        external
        inState(State.Voting)
    {
        require(!isVoted[msg.sender], "Already Voted");
        require(isCandidate[candidate], "Candidate not found");

        isVoted[msg.sender] = true;
        votesReceived[candidate] += 1;
    }

    function totalVotesFor(string calldata candidate)
        external
        view
        inState(State.Ended)
        returns (uint256)
    {
        require(isCandidate[candidate], "Candidate not found");
        return votesReceived[candidate];
    }
}
