import { FaFacebook } from "react-icons/fa6";
import { FaInstagram } from "react-icons/fa6";
import NavLinks from "./NavLinks";

const Footer = () => {
  return (
    <footer className="bg-primary-light py-8">
      <div className="container mx-auto flex justify-between">
        <div>
          <h2 className="text-lg font-medium">PetCare</h2>
          <ul className="mt-4 space-y-2">
            <NavLinks />
          </ul>
        </div>
        <div>
          <h2 className="text-lg font-medium">Stay Connected</h2>
          <p className="mt-4 text-gray-600">
            Contact: <br /> hi.petpeople@petcare.com
          </p>
          <div className="mt-2 flex gap-2">
            <a href="#" className="text-gray-600 hover:text-gray-800">
              <FaFacebook />
            </a>
            <a href="#" className="text-gray-600 hover:text-gray-800">
              <FaInstagram />
            </a>
          </div>
        </div>
        <div>
          <h2 className="text-lg font-medium">
            Join as petfamily and get 10% OFF
          </h2>
          <p className="mt-4 text-gray-600">
            Our services are wide open for you
          </p>
          <button className="mt-4 rounded-full border border-gray-300 bg-white px-4 py-2 transition duration-150 ease-in-out hover:bg-primary hover:text-white">
            Be Petfamily
          </button>
        </div>
      </div>
      <div className="container mx-auto mt-8 flex justify-between text-sm text-gray-600">
        <p>© PetCare.co</p>
        <a href="#" className="hover:text-gray-800">
          Terms and Privacy Policy
        </a>
      </div>
    </footer>
  );
};
export default Footer;
