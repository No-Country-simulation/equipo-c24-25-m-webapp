import Registrarse from "../components/Registrarse/Registrarse"


const page:React.FC = () => {


    
  return (
    <div className="flex flex-col md:flex-row justify-center items-center space-y-6 md:space-y-0 mt-4 md:mt-0 mb-4 md:mb-0">
     <img src="/img-register.png" alt="Img-register" className="md:w-[694px] md:h-[590px]" />
    <Registrarse/>
    </div>
  )
}

export default page