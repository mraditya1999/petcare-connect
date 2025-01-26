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
      <section className="bg-gray-50 py-8">
        <div className="container mx-auto px-4">
          <div className="section-width flex items-center justify-between">
            <div className="flex w-1/2 justify-center p-4">
              <div className="w-3/4">
                <h2 className="mb-4 text-4xl font-medium text-gray-800">
                  Prioritizing your pet companion
                </h2>
                <p className="text-md text-gray-600">
                  At pawcare, our primary goal is to ensure that every pet we
                  care for leads a happy, healthy life. We are dedicated to
                  providing the highest standard of veterinary care, delivered
                  with compassion and professionalism. Our team of experienced
                  veterinarians and support staff work tirelessly to promote
                  preventive care for your lovely pet, providing comprehensive
                  treatments and supporting through all life stages
                </p>
              </div>
            </div>
            <div className="flex w-1/2 justify-center overflow-hidden rounded-lg p-4">
              <img
                src={catImg}
                alt="Content"
                className={`h-auto w-full max-w-80 rounded-md object-cover shadow-lg`}
              />
            </div>
          </div>
        </div>
      </section>

      {/* Benefits */}
      <section className="bg-white py-16">
        <div className="section-width mx-auto flex flex-col justify-center px-16">
          <h1 className="mb-4 text-3xl font-bold uppercase">BENEFITS</h1>
          <div className="flex-between mx-auto h-96 w-full gap-6">
            <Card className="group relative h-full w-full overflow-hidden rounded-2xl">
              <img
                src={doctor1}
                alt="Professional Team"
                className="h-full w-full object-cover"
              />
              <div className="absolute inset-0 h-full bg-black opacity-30 transition-opacity duration-300 group-hover:opacity-0"></div>
              <CardContent className="absolute bottom-10 left-5 z-10 flex justify-center rounded-full bg-white px-3 py-2.5">
                <CardTitle className="text-md font-bold">
                  Professional Team
                </CardTitle>
              </CardContent>
            </Card>

            <Card className="group relative h-full w-full overflow-hidden rounded-2xl">
              <img
                src={doctor2}
                alt="Professional Team"
                className="h-full w-full object-cover"
              />
              <div className="absolute inset-0 h-full bg-black opacity-30 transition-opacity duration-300 group-hover:opacity-0"></div>
              <CardContent className="absolute bottom-10 left-5 z-10 flex justify-center rounded-full bg-white px-3 py-2.5">
                <CardTitle className="text-md font-bold">
                  Treat with <FaHeart className="inline text-red-500" />
                </CardTitle>
              </CardContent>
            </Card>

            <Card className="group relative h-full w-full overflow-hidden rounded-2xl">
              <img
                src={doctor3}
                alt="Professional Team"
                className="h-full w-full object-cover"
              />
              <div className="absolute inset-0 h-full bg-black opacity-30 transition-opacity duration-300 group-hover:opacity-0"></div>
              <CardContent className="absolute bottom-10 left-5 z-10 flex justify-center rounded-full bg-white px-3 py-2.5">
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
        <div className="section-width px-4">
          <div className="mx-auto flex items-center items-stretch justify-between rounded-3xl bg-[#182559] p-20 text-white">
            <div className="flex-1 px-12 py-12">
              <h1 className="mb-8 text-3xl capitalize">
                Our experts are available for you 24/7
              </h1>
              <div className="flex flex-col gap-2">
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
            </div>
            <div className="flex flex-1 items-center px-12">
              <img
                src={doctor4}
                alt="doctor smiling"
                className="w-md max-w-[20rem]"
              />
            </div>
          </div>
        </div>
      </section>
    </div>
  );
};
export default HomePage;
