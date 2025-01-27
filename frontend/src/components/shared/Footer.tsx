import { FaFacebook } from "react-icons/fa6";
import { FaInstagram } from "react-icons/fa6";
import NavLinks from "./NavLinks";
import { ROUTES } from "@/utils/constants";
import { Link } from "react-router-dom";

const Footer = () => {
  return (
    <footer className="bg-gray-50 py-16">
      <div className="container mx-auto flex justify-between px-20">
        <div>
          <h2 className="text-xl font-medium">PetCare</h2>
          <ul className="mt-4 space-y-2">
            <NavLinks />
          </ul>
        </div>
        <div>
          <h2 className="text-xl font-medium">Stay Connected</h2>
          <p className="mb-2 mt-4 text-gray-600">
            <span className="font-medium">Contact:</span> <br />{" "}
            hi.petpeople@petcare.com
          </p>
          <div className="mt-2 flex gap-3">
            <a
              href="#"
              className="transform text-2xl text-gray-600 duration-300 hover:text-primary"
            >
              <FaFacebook />
            </a>
            <a
              href="#"
              className="transform text-2xl text-gray-600 duration-300 hover:text-primary"
            >
              <FaInstagram />
            </a>
          </div>
        </div>
        <div>
          <h2 className="text-xl font-medium">
            Join as petfamily and get 10% OFF
          </h2>
          <p className="mb-4 mt-4 text-gray-600">
            Our services are wide open for you
          </p>
          <Link
            to={`${ROUTES.LOGIN}`}
            className="mt-4 rounded-lg bg-primary px-4 py-2 text-white shadow-sm transition duration-300 ease-in-out hover:bg-primary/90"
          >
            Be Petfamily
          </Link>
        </div>
      </div>
      <div className="container mx-auto mt-8 flex items-center gap-6 text-sm text-gray-600">
        <p>© PetCare.co</p>
        <a href="#" className="hover:text-primary">
          Terms and Privacy Policy
        </a>
      </div>
    </footer>
  );
};
export default Footer;
