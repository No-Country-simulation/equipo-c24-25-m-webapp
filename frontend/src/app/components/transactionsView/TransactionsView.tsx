"use client"

import Transaction from "../transaction/Transaction"




const TransactionsView: React.FC = () => {

    

    return (
        <div className="flex flex-col md:justify-between md:flex-row px-20 space-y-4 md:space-y-14 mb-20">

            <div className="flex flex-col justify-center md:justify-between items-center space-y-6">
                <p className="text-[22px]">Mi Tarjeta</p>
                <img src="/credit-card.png" alt="credit-card-mockup" className="w-[331px] h-[199px]" />

                <div className="flex flex-col justify-center items-center bg-white border-estadocuenta-border border-[1px] rounded-[20px] 
                w-[331px] h-[105px]
                ">
                    <h3 className="text-[16px] text-letraestadodecuenta">Estado de cuenta </h3>
                    <p className="text-[20px]">$1.280.000,32</p>
                </div>

                <div className="flex flex-col justify-center items-center space-y-2">

                    <button>
                        <div className="flex flex-row justify-between items-center w-[328px] h-[45px] bg-nav-buttons rounded-[30px] px-6 hover:bg-buttons-hover hover:text-white">
                            <p className="text-[16px]">Agregar Tarjeta</p>
                            <img src="mas-icon.png" alt="+ icon" className="w-[20px] h-[20px]" />
                        </div>
                    </button>

                    <button>
                        <div className="flex flex-row justify-between items-center w-[328px] h-[45px] bg-nav-buttons rounded-[30px] px-6 hover:bg-buttons-hover hover:text-white">
                            <p className="text-[16px]">Nueva Transferencia</p>
                            <img src="mas-icon.png" alt="+ icon" className="w-[20px] h-[20px]" />
                        </div>
                    </button>

                    <button>
                        <div className="flex flex-row justify-between items-center w-[328px] h-[45px] bg-nav-buttons rounded-[30px] px-6 hover:bg-buttons-hover hover:text-white">
                            <p className="text-[16px]">Realizar un pago</p>
                            <img src="mas-icon.png" alt="+ icon" className="w-[20px] h-[20px]" />
                        </div>
                    </button>

                    <p className="text-transfer-color text-[28px]">Transferencia</p>
                    <input type="text" placeholder="CBU" className="border-[1px] border-transfer-inputs rounded-[8px] w-[312px] h-[44px] placeholder:text-black" />
                    <input type="number" placeholder="MONTO" className="border-[1px] border-transfer-inputs rounded-[8px] w-[312px] h-[44px] placeholder:text-black" />
                    <button className="bg-nav-buttons w-[125px] h-[45px] rounded-[30px] hover:text-white hover:bg-buttons-hover"><p className="text-[11px]">Enviar Dinero</p></button>
                </div>

            </div>

            <div className="flex flex-col items-center md:mt-20 w-full">
                <div className="flex flex-col md:flex-row md:justify-between w-full px-11 justify-center items-center ">
                    <p className="text-[22px] text-nowrap">Historial de transacciones</p>
                    <button className="flex justify-center items-center bg-nav-buttons w-[86px] h-[45px] rounded-[30px] hover:text-white hover:bg-buttons-hover"><p className="text-[11px]">Ver Todo</p></button>
                </div>
            </div>

             <Transaction/>
        </div>
    )
}

export default TransactionsView