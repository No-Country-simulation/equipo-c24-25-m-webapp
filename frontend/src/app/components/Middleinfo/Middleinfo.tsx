//CSS
import "./Middle.css"
//Assets
import Image from "next/image"



const Middleinfo: React.FC = () => {
  return (

    <div>
      <section className=" md:space-y-20 mt-20 md:mt-[60px]">
        <div className="flex flex-col md:flex-row justify-center items-center space-x-4 md:space-x-32 md:max-w-[1039px] md:max-h-[226px] mx-auto">
          <Image height={126} width={126} src="/persona3.png" alt="img1 middle" className="md:w-[226px] md:h-[226px]"></Image>
          <div className="flex flex-col justify-center items-center space-y-3 md:flex-row ">
            <div className="flex flex-col justify-center items-center space-y-3">
              <h2 className="text-xl text-middle-title md:text-4xl md:mr-28">Paquete de productos</h2>
              <p className="w-full max-w-md text-center md:text-left text-sm md:text-xl">Optimizá tus recursos, reducí costos y llevá tu negocio o emprendimiento al siguiente nivel. Descubrí soluciones eficientes que te ayudarán a maximizar tu rentabilidad y hacer crecer tu proyecto de manera inteligente.</p>
            </div>
            <div className="flex md:flex-row justify-center items-center space-x-0.5 md:space-x-1 text-middle-title">

              <button className="learn-more">
                <span className="circle" aria-hidden="true">
                  <span className="icon arrow"></span>
                </span>
                <span className="button-text">Conocer mas</span>
              </button>

            </div>
          </div>

        </div>

        <div className="flex flex-col md:flex-row justify-center items-center space-x-4 md:space-x-32 mt-9 md:max-w-[1039px] md:max-h-[226px] mx-auto">
          <Image height={126} width={126} src="/persona4.png" alt="img1 middle" className="md:w-[226px] md:h-[226px]"></Image>
          <div className="flex flex-col justify-center items-center space-y-3 md:flex-row ">
            <div className="flex flex-col justify-center items-center space-y-3">
              <h2 className="text-xl text-middle-title md:text-4xl md:mr-28">¿Cómo evitar estafas?</h2>
              <p className="w-full max-w-md text-center md:text-left text-sm md:text-xl">Optimizá tus recursos, reducí costos y llevá tu negocio o emprendimiento al siguiente nivel. Descubrí soluciones eficientes que te ayudarán a maximizar tu rentabilidad y hacer crecer tu proyecto de manera inteligente..</p>
            </div>
            <div className="flex md:flex-row justify-center items-center space-x-0.5 md:space-x-1 text-middle-title">


              <button className="learn-more">
                <span className="circle" aria-hidden="true">
                  <span className="icon arrow"></span>
                </span>
                <span className="button-text">Conocer mas</span>
              </button>

            </div>
          </div>

        </div>

      </section>
    </div>
  )
}

export default Middleinfo