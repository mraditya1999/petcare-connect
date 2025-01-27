import headerImg from "@/assets/images/servicepage/header.png";
import image2 from "@/assets/images/servicepage/image2.png";
import image3 from "@/assets/images/servicepage/image3.png";
import { FaArrowRight } from "react-icons/fa";
import { ContentCard, Header } from "@/components";

const ServicePage = () => {
  return (
    <div>
      {/* Header */}
      <Header
        headerImage={headerImg}
        headerText={"Online and Offline Veterinary Services"}
      />

      <section className="bg-gray-50 py-16">
        <ContentCard
          image={image2}
          imageClass="rounded-xl"
          heading="Caring for Your Furry Friends - Join the Talk!"
          text=" The Pet Care Connect Forum is a community hub for pet owners and enthusiasts to share knowledge, seek advice, and connect with like-minded individuals. It offers expert tips, real-life experiences, and discussions on topics like pet health, nutrition, behavior, and training."
          isOdd={true}
          icon={<FaArrowRight />}
          buttonText="Your Contributions"
        />
      </section>
      <section className="py-16">
        <ContentCard
          image={image3}
          imageClass="rounded-xl"
          heading="Visit Our Clinic"
          text="Visit our clinic for comprehensive veterinary care. Our experienced team provides thorough physical examinations, advanced diagnostics, and personalized treatments to ensure your pet's health and well-being. Schedule your in-clinic appointment today for the best in pet care."
          isOdd={false}
          icon={<FaArrowRight />}
          buttonText="About Us"
        />
      </section>
    </div>
  );
};
export default ServicePage;
