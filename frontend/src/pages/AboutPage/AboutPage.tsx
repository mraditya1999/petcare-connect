// import React from "react";

import { Header } from "@/components";
import headerImg from "@/assets/images/aboutpage/Vet_header.jpg";
import img1 from "@/assets/images/aboutpage/Vet_service.jpg";
import { Button } from "@/components/ui/button";
import { FaArrowRight } from "react-icons/fa6";
import img2 from "@/assets/images/aboutpage/clinic_location.png";

const AboutPage = () => {
  return (
    <div>
      {/* Header */}
      <Header
        headerImage={headerImg}
        headerText={"Ensuring your pets live their best lives."}
      />

      {/* Services */}
      <section className="flex items-center justify-center bg-gray-50 py-16">
        <div className="section-width flex flex-col items-center justify-between gap-4">
          <article className="flex flex-col gap-4 p-4 md:flex-row md:px-16">
            <h2 className="flex-1 text-3xl font-semibold md:text-4xl">
              Providing best services for your pets
            </h2>
            <p className="flex-1 text-gray-400">
              At Pawcare, our in-clinic consultations offer comprehensive care
              to ensure your pet's health and well-being. Our experienced
              veterinarians conduct thorough physical examinations, use advanced
              diagnostic tools, and create personalized treatment plans tailored
              to your pet’s unique needs.
            </p>
          </article>
          <article className="h-64 w-full md:h-96 md:px-16">
            <img
              src={img1}
              alt="Vet checking dog"
              className="h-full w-full rounded-xl object-cover"
            />
          </article>
        </div>
      </section>

      {/* Clini llocation */}
      <section className="py-16">
        <div className="section-width mx-auto">
          <div className="px-6 md:px-20">
            <h1 className="mb-8 text-3xl font-semibold lg:text-4xl">
              Clinic Location
            </h1>
            <div className="flex flex-col gap-8 md:flex-row">
              <article className="flex">
                <img
                  src={img2}
                  alt="Veterinarian examining a dog"
                  className="h-80 w-full rounded-xl object-cover"
                />
              </article>
              <article className="flex flex-1 flex-col">
                <h1 className="mb-6 text-3xl font-semibold md:text-4xl">
                  Pawsville
                </h1>
                <p className="mb-6 max-w-xl text-gray-400">
                  Located at the heart of Pawsville, our clinic at 123 Pet Care
                  Lane offers a warm and welcoming environment for you and your
                  pet. With convenient access and ample parking, we’ve designed
                  our space to be as comfortable and stress-free as possible.
                  Drop by and let our dedicated team provide your furry friend
                  with top-notch care right in the heart of the community.
                </p>
                <Button className="flex gap-3 self-start rounded-full px-8 py-6">
                  Book Visit
                  <span>
                    <FaArrowRight />
                  </span>
                </Button>
              </article>
            </div>
          </div>
        </div>
      </section>
    </div>
  );
};

export default AboutPage;
