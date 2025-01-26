import React from "react";

import headerImg from "./images/Vet_header.jpg";
import img1 from "./images/Vet_service.jpg";
import img2 from "./images/clinic_location.png";
import { Header } from "@/components";
import { FaArrowRight } from "react-icons/fa";
import { Link } from "react-router-dom";
import { Button } from "@/components/ui/button";
const AboutPage = () => {
  return (
    <div>
      <Header
        headerImage={headerImg}
        headerText={"Pet care clinic welcoming environment"}
      />

      <section className="py-16">
        <div className="section-width mx-auto my-12 max-w-4xl rounded-lg p-8 shadow-lg">
          <div className="flex items-center justify-between gap-4">
            <h1 className="mb-4 flex-1 text-3xl font-bold">
              Providing best services for your pets
            </h1>
            <p className="mb-6 flex-1 text-center text-lg">
              At Pawcare, our in-clinic consultations offer comprehensive care
              to ensure your pet's health and well-being. Our experienced
              veterinarians conduct thorough physical examinations, use advanced
              diagnostic tools, and create personalized treatment plans tailored
              to your pet’s unique needs.
            </p>
          </div>
          <article>
            <img
              src={img1}
              alt="Veterinarian examining a dog"
              className="mx-auto rounded-lg"
            />
          </article>
        </div>
      </section>

      <section className="py-16">
        <div className="section-width mx-auto px-20">
          <h1 className="mb-4 text-4xl font-medium">Clinic Location</h1>
          <div className="flex gap-12">
            <article className="flex h-96 items-center justify-start">
              <img
                src={img2}
                alt="Veterinarian examining a dog"
                className="h-full rounded-lg"
              />
            </article>
            <article className="flex flex-1 flex-col items-start justify-center py-20">
              <h1 className="mb-6 text-3xl font-bold">Pawsville</h1>
              <p className="mb-6">
                Located at the heart of Pawsville, our clinic at 123 Pet Care
                Lane offers a warm and welcoming environment for you and your
                pet. With convenient access and ample parking, we’ve designed
                our space to be as comfortable and stress-free as possible. Drop
                by and let our dedicated team provide your furry friend with
                top-notch care right in the heart of the community.
              </p>
              <Button className="flex gap-3 self-start">
                Book Visit
                <span>
                  <FaArrowRight />
                </span>
              </Button>
            </article>
          </div>
        </div>
      </section>
    </div>
  );
};

export default AboutPage;
