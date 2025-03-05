'use client'

//Assets
import Image from 'next/image'
import Link from 'next/link'
import { useState } from 'react'
//Libraries
import { Menu, X } from 'lucide-react'

const Navbar: React.FC = () => {
  const [isOpen, setIsopen] = useState(false)

  return (
    <div>
      {/* MOBILE NAV BAR*/}
      <nav className="md:hidden">
        <li className="flex justify-center items-center md:hidden">
          {' '}
          <Link href="/">
            <Image height={178} width={102} src="/LOGO.png" alt="Logo"></Image>
          </Link>
        </li>
        <button
          className="absolute top-5 right-5 z-50 md:hidden"
          onClick={() => setIsopen(!isOpen)}
        >
          {isOpen ? (
            <X size={30} className="text-lime-500" />
          ) : (
            <Menu size={30} className="text-lime-500" />
          )}
        </button>

        <div
          className={`flex justify-center items-center fixed top-0 left-0 h-screen w-screen bg-black text-white transition-transform duration-300 z-30
        ${isOpen ? 'translate-x-0' : '-translate-x-full'}
        md:relative md:translate-x-0 md:h-auto md:w-auto md:bg-transparent
        `}
        >
          <ul className="flex flex-col justify-center items-center space-y-2 w-[90%] ">
            <li>
              {' '}
              <Link href="/">
                <Image
                  height={178}
                  width={102}
                  src="/LOGO.png"
                  alt="Logo"
                ></Image>
              </Link>
            </li>
            <li className="flex justify-center items-center bg-nav-buttons p-3 m-3 rounded-lg w-full h-[40px]">
              <Link href="/">Empresas</Link>
            </li>
            <li className="flex justify-center items-center bg-nav-buttons p-3 m-3 rounded-lg w-full h-[40px]">
              <Link href="/">Personas</Link>
            </li>
            <li className="flex justify-center items-center bg-nav-buttons p-3 m-3 rounded-lg w-full h-[40px]">
              <Link href="/">Turnos web</Link>
            </li>
            <li className="flex justify-center items-center bg-nav-buttons p-3 m-3 rounded-lg w-full h-[40px]">
              <Link href="/">Ayuda</Link>
            </li>
            <li className="flex justify-center items-center bg-nav-buttons p-3 m-3 rounded-lg w-full h-[40px]">
              <Link href="/">Homebanking</Link>
            </li>
            <li className="flex justify-center items-center bg-nav-buttons p-3 m-3 rounded-lg w-full h-[40px] ">
              <Link href="/registro">Registrate</Link>
            </li>
          </ul>
        </div>
      </nav>

            {/* DESKTOP NAVBAR*/}
            <div className='flex justify-center items-center'>
            <nav className="hidden md:block max-w-[1200px] max-h-[105px]">
                <ul className="flex justify-between items-center w-full">
                    <li> <Link href="/"><Image height={189} width={130} src="/LOGO.png" alt="Logo" className="ml-4"></Image></Link></li>
                    <div className="flex space-x-1 ml-32">
                        <li className="flex justify-center items-center w-[114px] h-[27px]  hover:text-green-hover "><Link href="/">Empresas</Link></li>
                        <li className="flex justify-center items-center w-[114px] h-[27px]  hover:text-green-hover"><Link href="/">Personas</Link></li>
                        <li className="flex justify-center items-center w-[114px] h-[27px]  hover:text-green-hover"><Link href="/">Turnos web</Link></li>
                        <li className="flex justify-center items-center w-[114px] h-[27px]  hover:text-green-hover"><Link href="/">Ayuda</Link></li>
                    </div>

                    <div className="flex mr-4">
                        <li><button className="bg-nav-buttons hover:bg-buttons-hover p-3 m-3 rounded-full w-[125px] h-[45px] flex justify-center items-center hover:text-white active:shadow-[0px_30px_10px_-20px_#119E1F] transition-colors duration-300"><Link href="/">Homebanking</Link></button></li>
                        <Link href="/registro" className=""><button className="bg-nav-buttons hover:bg-buttons-hover p-3 m-3 rounded-full w-[125px] h-[45px] flex justify-center items-center hover:text-white active:shadow-[0px_30px_10px_-20px_#119E1F] transition-colors duration-300">Registrate</button></Link>
                    </div>

                </ul>
            </nav>
            </div>

        </div>
    )
}

export default Navbar
