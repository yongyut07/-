# Voting DApp

เว็บไซต์สำหรับเชื่อมต่อกับ Smart Contract `Voting` ผ่าน MetaMask และ ethers.js

## ความสามารถ

- เชื่อมต่อ MetaMask
- ใส่ Contract Address หลัง Deploy
- อ่านรายชื่อผู้สมัครจาก Smart Contract
- เริ่มการโหวตด้วยบัญชีผู้ Deploy
- ลงคะแนนให้ผู้สมัคร
- ป้องกันการโหวตซ้ำตาม Smart Contract
- จบการโหวตด้วยบัญชีผู้ Deploy
- แสดงคะแนนหลังสถานะเป็น Ended

## วิธีใช้งาน

1. เปิด Remix IDE
2. Compile Smart Contract ด้วย Solidity 0.8.17
3. Deploy โดยใส่รายชื่อผู้สมัคร เช่น

```text
["Candidate A", "Candidate B", "Candidate C"]
```

4. คัดลอก Contract Address
5. เปิดโฟลเดอร์เว็บไซต์ด้วย Live Server
6. เชื่อม MetaMask และวาง Contract Address
7. กดโหลด Contract

## การเปิดเว็บ

แนะนำให้ใช้ Visual Studio Code และส่วนเสริม Live Server

- คลิกขวาไฟล์ `index.html`
- เลือก `Open with Live Server`

ไม่ควรเปิดด้วย `file://` โดยตรง เพราะบางเบราว์เซอร์อาจจำกัดการทำงานของ Web3

## หมายเหตุสำคัญเกี่ยวกับ Smart Contract เดิม

ฟังก์ชัน `voteForCandidate(string candidate)` ยังไม่ได้ตรวจสอบว่า candidate เป็นรายชื่อที่มีอยู่จริง ผู้ใช้อาจส่งชื่ออื่นผ่านเครื่องมือภายนอกได้ แม้หน้าเว็บนี้จะให้เลือกเฉพาะรายชื่อจริง

ควรเพิ่ม mapping ตรวจสอบผู้สมัครใน Smart Contract สำหรับงานที่ต้องการความปลอดภัยมากขึ้น
