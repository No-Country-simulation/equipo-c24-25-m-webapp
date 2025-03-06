import { useState,useEffect } from "react"
import { ITransaction } from "@/src/types/ITransaction"
import axios from "axios"


const Transaction:React.FC=() => {
    const [transactions, setTransactions] = useState<ITransaction[]>([])
    const accessToken = localStorage.getItem("access_token")
    useEffect(() => {
        const fetchTransactions = async () => {
            try {
                const response = await axios.get("https://equipo-c24-25-m-webapp-1.onrender.com/api/banca/transacciones/obtener-transacciones",{
                    headers:{
                          Authorization: `Bearer ${accessToken}`
                    }
                });
                const data = response.data;
                setTransactions(data);

                setTransactions(data);
            } catch (error) {
                console.log(error);
            }
        }
        fetchTransactions();
    }, [])
  return (
    <div className="">
        {transactions.map((transaction,id)=>
          <div className="flex flex-row justify-center items-center space-x-6 mt-20 text-sm" key={id}>
           <p>Amount:{transaction.amount}</p>
           <p>Date:{transaction.date}</p>
           <p>Receiver:{transaction.receiverId}</p>
          </div>
        )}
    
    </div>
  )
}

export default Transaction