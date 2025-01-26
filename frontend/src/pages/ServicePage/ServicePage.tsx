import React from "react";
import image1 from "@/assets/images/image1.jpg";
import image2 from "@/assets/images/image2.png";
import image3 from "@/assets/images/image3.png";
import image4 from "@/assets/images/image4.png";



const ServicePage = () => {
  return( 
    <div className="bg-gray-50 min-h-screen">
    {/* Head */}
   
       {/* Top Content */}
<section className="relative">
  <div className="relative h-[400px] bg-gray-200">
    <img
      src={image1}
      alt="Veterinary Service"
      className="w-full h-full object-cover"  
    />
  </div>
  <div className="absolute top-0 left-0 w-full h-full backdrop-blur-sm bg-black/30">
  <div className="absolute left-8" style={{ top: '250px' }}>
      <h4 className="text-white text-3xl md:text-4xl lg:text-5xl font-bold">
        Convenient Online and <br /> Offline Veterinary Services
      </h4>
    </div>
  </div>
</section>


 {/* Content Sections */}
 <main className="container mx-auto py-12 px-6">
        {/* Forum Section */}
        {/* <div className="flex flex-col lg:flex-row items-center gap-12 mb-16">
          <div className="lg:w-1/2">
            <h3 className="text-2xl font-semibold mb-4">
              Caring for Your Furry Friends - Join the Talk!
            </h3>
            <p className="text-gray-600 mb-6">
              The Pet Care Connect Forum is a community hub for pet owners and enthusiasts to share knowledge, seek advice, and connect with like-minded individuals. It offers expert tips, real-life experiences, and discussions on topics like pet health, nutrition, behavior, and training.
            </p>
            <button className="text-white bg-blue-600 hover:bg-blue-700 py-2 px-4 rounded-full">
              Your Contributions
            </button>
          </div>
          <div className="lg:w-1/2">
            <img
              src={image2}
              alt="Forum Discussion"
              className="rounded-lg shadow-md w-full h-auto object-cover"
      style={{ aspectRatio: '16/9' }}
            />
          </div>
        </div> */}
        <div className="flex flex-col lg:flex-row items-center gap-12 mb-16">
  <div className="lg:w-1/2">
    <h3 className="text-3xl md:text-4xl lg:text-5xl font-semibold mb-6"> {/* Increased text size */}
      Caring for Your Furry Friends - Join the Talk!
    </h3>
    <p className="text-gray-600 text-lg md:text-xl lg:text-2xl mb-4"> {/* Increased text size */}
      The Pet Care Connect Forum is a community hub for pet owners and enthusiasts to share knowledge, seek advice, and connect with like-minded individuals. It offers expert tips, real-life experiences, and discussions on topics like pet health, nutrition, behavior, and training.
    </p>
    <button className="text-white bg-blue-600 hover:bg-blue-700 py-2 px-4 rounded-full">
      Your Contributions
    </button>
  </div>
  <div className="lg:w-1/2">
    <img
      src={image2}
      alt="Forum Discussion"
      className="rounded-lg shadow-md w-full h-auto object-cover"
      style={{ aspectRatio: '16/9' }}
    />
  </div>
</div>


  {/* Clinic Section */}
  <div className="flex flex-col lg:flex-row-reverse items-center gap-12">
  <div className="lg:w-1/2">
    <h3 className="text-2xl font-semibold mb-4">Visit Our Clinic</h3>
    <p className="text-gray-600 mb-6">
      Visit our clinic for comprehensive veterinary care. Our experienced team provides thorough physical examinations, advanced diagnostics, and personalized treatments to ensure your pet's health and well-being. Schedule your in-clinic appointment today for the best in pet care.
    </p>
    <button className="text-white bg-blue-600 hover:bg-blue-700 py-2 px-4 rounded-full">
      About Us
    </button>
  </div>
  <div className="lg:w-1/2 w-full">
    <img
      src={image3}
      alt="Clinic Services"
      className="rounded-lg shadow-md w-full min-h-[300px] object-cover"
    />
  </div>
</div>

{/* <div className="flex flex-col lg:flex-row-reverse items-center gap-12">
  <div className="lg:w-1/2">
    <h3 className="text-3xl md:text-4xl lg:text-5xl font-semibold mb-6"> 
      Visit Our Clinic
    </h3>
    <p className="text-gray-600 text-lg md:text-xl lg:text-2xl mb-4"> 
      Visit our clinic for comprehensive veterinary care. Our experienced team provides thorough physical examinations, advanced diagnostics, and personalized treatments to ensure your pet's health and well-being. Schedule your in-clinic appointment today for the best in pet care.
    </p>
    <button className="text-white bg-blue-600 hover:bg-blue-700 py-2 px-4 rounded-full">
      About Us
    </button>
  </div>
  <div className="lg:w-1/2 w-full">
    <img
      src={image3}
      alt="Clinic Services"
      className="rounded-lg shadow-md w-full min-h-[300px] object-cover"
    />
  </div>
</div> */}


      </main>



    </div>
  );

};
export default ServicePage;
