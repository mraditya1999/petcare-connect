import { FaPhoneVolume, FaHeart } from "react-icons/fa6";
import { MdEmail } from "react-icons/md";
import { Card, CardContent, CardTitle } from "@/components/ui/card";
import { Header } from "@/components";

import catImg from "@/assets/images/homepage/cat sleeping.png";
import headerImg from "@/assets/images/homepage/header.png";
import doctor1 from "@/assets/images/homepage/doctor1.png";
import doctor2 from "@/assets/images/homepage/doctor2.png";
import doctor3 from "@/assets/images/homepage/doctor3.png";
import doctor4 from "@/assets/images/homepage/doctor4.png";

const HomePage = () => {
  return (
    <div>
      {/* Header */}
      <Header
        headerImage={headerImg}
        headerText={"Ensuring your pets live their best lives."}
      />

      {/* Introduction */}
      <section className="flex items-center justify-center bg-gray-50 py-16">
        <div className="section-width flex flex-col items-center justify-between gap-4 md:flex-row">
          <article className="flex flex-1 flex-col justify-center px-4">
            <h2 className="mb-4 text-3xl font-medium text-gray-800 sm:text-4xl">
              Prioritizing your pet companion
            </h2>
            <p className="text-md max-w-xl text-gray-600">
              At petcare, our primary goal is to ensure that every pet we care
              for leads a happy, healthy life. We are dedicated to providing the
              highest standard of veterinary care, delivered with compassion and
              professionalism. Our team of experienced veterinarians and support
              staff work tirelessly to promote preventive care for your lovely
              pet, providing comprehensive treatments and supporting through all
              life stages
            </p>
          </article>
          <article className="mx-4 flex max-h-64 w-full flex-1 items-center justify-center overflow-hidden rounded-lg">
            <img
              src={catImg}
              alt="Content"
              className={`block h-full w-full overflow-hidden rounded-lg object-cover shadow-xl`}
            />
          </article>
        </div>
      </section>

      {/* Benefits */}
      <section className="bg-white py-16">
        <div className="section-width mx-auto flex flex-col justify-center">
          <h1 className="mb-4 text-4xl font-bold uppercase md:text-3xl">
            BENEFITS
          </h1>
          <div className="flex-between mx-auto w-full flex-col gap-3 md:h-96 md:flex-row">
            <Card className="group relative h-72 w-full overflow-hidden rounded-2xl md:h-full">
              <img
                src={doctor1}
                alt="Professional Team"
                className="h-full w-full object-cover"
              />
              <div className="absolute inset-0 h-full bg-black opacity-30 transition-opacity duration-300 group-hover:opacity-0"></div>
              <CardContent className="absolute bottom-10 left-5 z-10 flex justify-center rounded-full bg-white px-5 py-2.5">
                <CardTitle className="text-md font-bold">
                  Professional Team
                </CardTitle>
              </CardContent>
            </Card>

            <Card className="group relative h-72 w-full overflow-hidden rounded-2xl md:h-full">
              <img
                src={doctor2}
                alt="Professional Team"
                className="h-full w-full object-cover"
              />
              <div className="absolute inset-0 h-full bg-black opacity-30 transition-opacity duration-300 group-hover:opacity-0"></div>
              <CardContent className="absolute bottom-10 left-5 z-10 flex justify-center rounded-full bg-white px-5 py-2.5">
                <CardTitle className="text-md font-bold">
                  Treat with <FaHeart className="inline text-red-500" />
                </CardTitle>
              </CardContent>
            </Card>

            <Card className="group relative h-72 w-full overflow-hidden rounded-2xl md:h-full">
              <img
                src={doctor3}
                alt="Professional Team"
                className="h-full w-full object-cover"
              />
              <div className="absolute inset-0 h-full bg-black opacity-30 transition-opacity duration-300 group-hover:opacity-0"></div>
              <CardContent className="absolute bottom-10 left-5 z-10 flex justify-center rounded-full bg-white px-5 py-2.5">
                <CardTitle className="text-md font-bold">
                  Emergency Care
                </CardTitle>
              </CardContent>
            </Card>
          </div>
        </div>
      </section>

      {/* Contact  */}
      <section className="bg-white py-16">
        <div className="section-width">
          <div className="flex flex-col items-center justify-between gap-8 rounded-3xl bg-[#182559] p-8 text-white sm:p-16 md:flex-row">
            <article className="flex-1 self-start">
              <h1 className="mb-8 text-2xl capitalize md:text-3xl lg:text-4xl">
                Our experts are available for you 24/7
              </h1>
              <div className="flex flex-col gap-2 lg:text-lg">
                <p className="flex items-center gap-3">
                  <FaPhoneVolume className="text-red-500" /> 62 21345 8888
                </p>
                <p className="flex items-center gap-3">
                  <FaPhoneVolume className="text-red-500" /> 62 21345 4444{" "}
                  <br />
                  (Emergency Services)
                </p>
                <p className="flex items-center gap-3">
                  <MdEmail /> mail@petcare.com
                </p>
              </div>
            </article>
            <article className="flex max-h-96 flex-1 items-center justify-center overflow-hidden rounded-2xl">
              <img
                src={doctor4}
                alt="doctor smiling"
                className="h-full w-72 rounded-2xl object-cover md:w-full"
              />
            </article>
          </div>
        </div>
      </section>
    </div>
  );
};
export default HomePage;
