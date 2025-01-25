import catImg from "@/assets/images/homepage/cat sleeping.png";
import { Header } from "@/components";
import headerImg from "@/assets/images/homepage/header.png";
import ContentCard from "@/components/shared/ContentCard";
const HomePage = () => {
  return (
    <div>
      <Header
        headerImage={headerImg}
        headerText={"Ensuring your pets live their best lives."}
      />
      <section className="bg-gray-50 py-8">
        <div className="container mx-auto px-4">
          <ContentCard
            image={catImg}
            heading="Prioritizing your pet companion"
            text="At pawcare, our primary goal is to ensure that every pet we care for leads a happy, healthy life. We are dedicated to providing the highest standard of veterinary care, delivered with compassion and professionalism.   Our team of experienced veterinarians and support staff work tirelessly to promote preventive care for your lovely pet, providing comprehensive treatments and supporting through all life stages "
            isOdd={true}
            imageClass="rounded-md max-w-80"
          />
        </div>
      </section>
    </div>
  );
};
export default HomePage;
