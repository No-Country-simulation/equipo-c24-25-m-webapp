


const VideoPromotion: React.FC = () => {


    return (
        <div className="flex justify-center items-center mt-28 mb-28">
            <div className="flex flex-col justify-center items-center space-y-2">

                <h3 className="text-middle-title text-xl md:text-4xl">Conoce mas sobre el mundo luma.</h3>
                <video src="/video1promo.mp4"  className="w-full h-auto rounded-2xl" autoPlay muted loop></video>
               
            </div>
        </div>
    )
}

export default VideoPromotion