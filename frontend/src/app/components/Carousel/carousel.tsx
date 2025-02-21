"use client"
//Libraries 
//Assets
import Image from "next/image";
import { Swiper, SwiperSlide } from "swiper/react";
import "swiper/css";

const Swipercarousel: React.FC = () => {
    return (
        <div className="flex justify-center items-center mt-28"> 
            <Swiper spaceBetween={10} slidesPerView={1} loop={true} className="md:w-[1200px] md:h-[375px] rounded-2xl">
                <SwiperSlide><Image alt="" src="/carousel-1.png" objectFit="cover" width={1200} height={300}></Image></SwiperSlide>
                <SwiperSlide><Image alt="" src="/carousel-2.png" objectFit="cover" width={1200} height={300}></Image></SwiperSlide>
                <SwiperSlide><Image alt="" src="/carousel-3.png" objectFit="cover" width={1200} height={300}></Image></SwiperSlide>
            </Swiper>
        </div>
    )
}

export default Swipercarousel