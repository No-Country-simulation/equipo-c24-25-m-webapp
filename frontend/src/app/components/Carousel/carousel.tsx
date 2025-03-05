"use client";
import { useState } from "react";
import Image from "next/image";

const MyCarousel = () => {
  const [currentIndex, setCurrentIndex] = useState(0);
  const images = [
    "/flayer1.png",
    "/flayer2.png",
    "/flayer3.png",
  ];

  const handleNext = () => {
    setCurrentIndex((prevIndex) => (prevIndex + 1) % images.length);
  };

  const handlePrev = () => {
    setCurrentIndex((prevIndex) =>
      prevIndex === 0 ? images.length - 1 : prevIndex - 1
    );
  };

  return (
   
      <div id="default-carousel" className="relative max-w-[1200px] mt-6 mx-auto max-h-[406px] " data-carousel="slide">
        {/* Carousel wrapper */}
        <div className=" h-36 overflow-hidden rounded-lg md:h-[500px]">
          {/* Current Image with transition effect */}
          {images.map((src, index) => (
            <div
              key={index}
              className={`absolute w-full h-full transition-opacity duration-700 ease-in-out ${currentIndex === index ? "opacity-100" : "opacity-0"
                }`}
              data-carousel-item
            >
              <Image
                src={src}
                width={1200}
                height={406}
                alt={`Slide ${index + 1}`}
                className="absolute block object-cover w-full"
              />
            </div>
          ))}
        </div>

        {/* Slider controls */}
        <button
          type="button"
          className="absolute top-1/2 left-0 z-20 flex items-center justify-center h-full px-4 cursor-pointer group focus:outline-none transform -translate-y-1/2"
          onClick={handlePrev}
        >
          <span className="inline-flex items-center justify-center  w-6 h-6 md:w-10 md:h-10 rounded-full bg-white/30 group-hover:bg-white/50 md:mb-20 mb-12">
            <img src="/arrow-left.svg" alt="arrow left" />
          </span>
        </button>
        <button
          type="button"
          className="absolute top-1/2 right-0 z-20 flex items-center justify-center h-full px-4 cursor-pointer group focus:outline-none transform -translate-y-1/2"
          onClick={handleNext}
        >
          <span className="inline-flex items-center justify-center w-6 h-6 md:w-10 md:h-10 rounded-full bg-white/30 group-hover:bg-white/50 md:mb-20 mb-12">
            <img src="/arrow-right.svg" alt="arrow right" />
          </span>
        </button>
      </div>
    
  );
};

export default MyCarousel;
