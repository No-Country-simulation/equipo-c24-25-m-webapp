//Components
import Swipercarousel from "./components/Carousel/carousel";


export default function Home() {
  return (
    <div>
      <div className="bg-black-purple-gradient h-28"><h1 className="text-white text-4xl text-center">NAVBAR</h1></div>
      <Swipercarousel />
      <br /><br /><br /><br /><br /><br />

      
       <div className="bg-black-purple-gradient w-full h-auto flex flex-col justify-center items-center md:space-x-10 md:flex-row md:h-72">
       
       <h2 className="text-white font-sans md:text-2xl">Conoce todo lo que tenemos para brindarte</h2>
       <hr className="w-full border-l-2 border-line-color md:hidden"/>  

       <div className="border-l-2 border-line-color h-44 hidden md:block"></div>

       <div className="flex justify-center items-center mt-4 sm:space-x-9 space-x-4">
        
        <div className="flex flex-col justify-center items-center">
          <img src="/payment-icon.svg" alt="payment icon" className="w-14 hover:scale-110 transition-transform duration-300 cursor-pointer hover:invert"/>
          <h1 className="text-white text-center md:text-xl">Tarjetas de credito</h1>
        </div>

        <div className="flex flex-col justify-center items-center">
          <img src="/signal-icon.svg" alt="payment icon" className="w-14 hover:scale-110 transition-transform duration-300 cursor-pointer hover:invert"/>
          <h1 className="text-white text-center md:text-xl">Pagos y transferencias</h1>
        </div>

        <div className="flex flex-col jusrif-center items-center">
          <img src="/support-icon.svg" alt="payment icon" className="w-14 hover:scale-110 transition-transform duration-300 cursor-pointer hover:invert"/>
          <h1 className="text-white text-center md:text-xl">Soporte las 24hs</h1>
        </div>

       </div>

       </div>
<br /><br /><br /><br /><br /><br /><br /><br /><br /><br />
<div className="bg-black-purple-gradient h-28 mt-6"><h1 className="text-white text-4xl text-center">FOOTERrrrr</h1></div>
     </div>

    
  );
}
