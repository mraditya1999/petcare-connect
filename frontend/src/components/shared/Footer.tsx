import { FaFacebook, FaInstagram } from "react-icons/fa6";
import NavLinks from "./NavLinks";
import { ROUTES } from "@/utils/constants";
import { Link } from "react-router-dom";

const Footer = () => {
  return (
    <footer className="bg-secondary py-16 text-foreground">
      <div className="section-width mx-auto flex flex-col justify-between gap-16 p-6 md:flex-row lg:px-20">
        {/* Section 1 */}
        <article>
          <h2 className="text-xl font-medium">PetCare</h2>
          <ul className="mt-2 space-y-2 text-muted-foreground">
            <NavLinks />
          </ul>
        </article>

        {/* Section 2 */}
        <article>
          <h2 className="text-xl font-medium">Stay Connected</h2>
          <p className="mb-2 mt-4 text-muted-foreground">
            <span className="font-medium">Contact:</span> <br />
            hi.petpeople@petcare.com
          </p>

          <div className="mt-2 flex gap-3">
            <a
              href="#"
              className="transform text-2xl text-muted-foreground duration-300 hover:text-primary"
            >
              <FaFacebook />
            </a>
            <a
              href="#"
              className="transform text-2xl text-muted-foreground duration-300 hover:text-primary"
            >
              <FaInstagram />
            </a>
          </div>
        </article>

        {/* Section 3 */}
        <article>
          <h2 className="text-xl font-medium">
            Join as petfamily and get 10% OFF
          </h2>
          <p className="mb-4 mt-4 text-muted-foreground">
            Our services are wide open for you
          </p>

          <Link
            to={ROUTES.LOGIN}
            className="mt-4 rounded-lg bg-primary px-4 py-2 text-primary-foreground shadow-sm transition duration-300 ease-in-out hover:bg-primary/90"
          >
            Be Petfamily
          </Link>
        </article>
      </div>

      <div className="section-width flex flex-col gap-2 p-6 text-sm text-muted-foreground md:flex-row md:items-center">
        <p>© PetCare.co</p>
        <a href="#" className="hover:text-primary">
          Terms and Privacy Policy
        </a>
      </div>
    </footer>
  );
};

export default Footer;
