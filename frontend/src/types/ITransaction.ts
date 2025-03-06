export interface ITransaction{
    senderId: number,
    receiverId: number,
    amount: number,
    date: string,
    status: string,
    transactionType: string
}