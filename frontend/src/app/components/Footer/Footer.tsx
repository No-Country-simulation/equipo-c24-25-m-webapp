import Link from "next/link"



const Footer: React.FC = () => {

    return (
        <div className="grid justify-items-center items-center">
            <div className="grid justify-between grid-cols-2 gap-5 md:flex-row md:flex mb-7 w-full md:max-w-[1381px] md:max-h-[323px]">

                <ul className="space-y-2 flex justify-start text-center md:text-left flex-col mx-5">
                    <li><h4 className="text-buttons-hover underline  decoration-buttons-hover text-[20px] text-center md:text-left">FUNCIONALIDADES</h4></li>
                    <li><p className="text-[14px] hover:text-buttons-hover cursor-pointer">Transferencias</p></li>
                    <li><p className="text-[14px] hover:text-buttons-hover cursor-pointer">Caja de Ahorro</p></li>
                    <li><p className="text-[14px] hover:text-buttons-hover cursor-pointer">Compras</p></li>
                    <li><p className="text-[14px] hover:text-buttons-hover cursor-pointer">Cobros</p></li>
                </ul>

                <ul className="space-y-2 flex justify-start text-center md:text-left flex-col mx-5">
                    <li><h4 className="text-buttons-hover underline  decoration-buttons-hover text-[20px] text-center md:text-left md:whitespace-nowrap">SOBRE LUMA</h4></li>
                    <li><p className="text-[14px] hover:text-buttons-hover cursor-pointer">Nosotros</p></li>
                    <li><p className="text-[14px] hover:text-buttons-hover cursor-pointer">Prensa</p></li>
                    <li><p className="text-[14px] hover:text-buttons-hover cursor-pointer">Trabajá con nosotros</p></li>
                    <li><p className="text-[14px] hover:text-buttons-hover cursor-pointer">Postulaciones</p></li>
                </ul>

                <ul className="space-y-2 flex justify-start text-center md:text-left flex-col mx-5">
                    <li><h4 className="text-buttons-hover underline  decoration-buttons-hover text-[20px] text-center md:text-left md:whitespace-nowrap">COSTOS Y LEGALES</h4></li>
                    <li><p className="text-[14px] hover:text-buttons-hover cursor-pointer">Costos</p></li>
                    <li><p className="text-[14px] hover:text-buttons-hover cursor-pointer">Politicas de privacidad</p></li>
                    <li><p className="text-[14px] hover:text-buttons-hover cursor-pointer">Información al usuario </p></li>
                    <li><p className="text-[14px] hover:text-buttons-hover cursor-pointer">Información general</p></li>
                </ul>

                <div className="flex flex-col space-y-4">
                    <ul className="space-y-2 flex justify-start text-center md:text-left flex-col mx-5">
                        <li><h4 className="text-buttons-hover underline  decoration-buttons-hover text-[20px] text-center md:text-left md:whitespace-nowrap">SOPORTE TÉCNICO</h4></li>
                        <li><p className="text-[14px] hover:text-buttons-hover cursor-pointer">Preguntas frecuentes</p></li>
                        <li><p className="text-[14px] hover:text-buttons-hover cursor-pointer">Consejos de seguridad</p></li>

                    </ul>

                    <ul className="space-y-2 flex justify-start text-center md:text-left flex-col mx-5">
                        <li><h4 className="text-buttons-hover underline  decoration-buttons-hover text-[20px] text-center md:text-left md:whitespace-nowrap">REDES SOCIALES </h4></li>
                        <div className="flex flex-row md:justify-center items-center justify-center space-x-4 ">
                            <div className="bg-instagram-gradient rounded-full cursor-pointer hover:scale-110 md:w-[55px] md:h-[55px] w-[50px] h-[45px] flex justify-center items-center"><img src="/insta-icon.svg" alt="instagram-icon" className="w-[35px] h-[35px]" /></div>
                            <div className="bg-linkedin-bg rounded-full cursor-pointer hover:scale-110 md:w-[55px] md:h-[55px] w-[50px] h-[45px] flex justify-center items-center"><img src="/linkedin-icon.svg" alt="linkedin-icon" className="w-[35px] h-[35px]" /></div>
                            <div className="bg-youtube-bg rounded-full cursor-pointer hover:scale-110 md:w-[55px] md:h-[55px] w-[50px] h-[45px] flex justify-center items-center"><img src="/youtube-icon.svg" alt="youtube-icon" className="w-[35px] h-[35px]" /></div>
                        </div>


                    </ul>
                </div>

                <ul className="space-y-2 flex justify-center items-center md:text-left flex-col mx-5">
                    <li><h4 className="text-buttons-hover underline  decoration-buttons-hover text-[20px] text-center md:whitespace-nowrap">DESCARGÁ LA APP</h4></li>
                    <li>
                        <button className="group bg-buttons-hover rounded-full w-[174px] h-[60px] text-white hover:bg-white hover:text-black flex items-center justify-center space-x-2 border-2 border-buttons-hover">
                            {/* SVG normal */}
                            <img
                                src="/apple-icon.svg"
                                alt="Icono normal"
                                className="w-6 h-6 group-hover:hidden"
                            />
                            {/* SVG en hover */}
                            <img
                                src="/apple-black-icon.svg"
                                alt="Icono hover"
                                className="w-6 h-6 hidden group-hover:block"
                            />
                            <div>
                                <h5 className="text-[14px]">Download from</h5>
                                <p className="text-[19px]">App store</p>
                            </div>
                        </button>
                    </li>

                    <li>
                        <button className="group bg-buttons-hover rounded-full w-[174px] h-[60px] text-white hover:bg-white hover:text-black flex items-center justify-center space-x-2 border-2 ">
                            {/* SVG normal */}
                            <img
                                src="/googleplay-icon.svg"
                                alt="Icono normal"
                                className="w-6 h-6 group-hover:hidden"
                            />
                            {/* SVG en hover */}
                            <img
                                src="/googleplay-black-icon.svg"
                                alt="Icono hover"
                                className="w-6 h-6 hidden group-hover:block"
                            />
                            <div>
                                <h5 className="text-[14px]">GET IT ON</h5>
                                <p className="text-[19px]">Google play</p>
                            </div>
                        </button>
                    </li>

                </ul>
            </div>
            <div className="flex justify-center items-center md:flex md:justify-start"> <Link href={"/"}><img src="/LOGO.png" alt="" className="w-[178px] h-[102px]"/></Link></div>
        </div>
    )
}

export default Footer