//Components
import Swipercarousel from "./components/Carousel/carousel";
import Middleinfo from "./components/Middleinfo/Middleinfo";
import Separacionseccion from "./components/Separacionseccion/Separacionseccion";
import VideoPromotion from "./components/Video/VideoPromotion";
import Separacionseccion2 from "./components/Separacionseccion2/Separacionseccion2";
export default function Home() {
  return (
    <div>
      <title>Home</title>


      <Swipercarousel />
      <Middleinfo />
      <Separacionseccion />
      <VideoPromotion />
      <Separacionseccion2 />
    </div>

  );
}
