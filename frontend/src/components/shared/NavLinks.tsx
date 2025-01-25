import { ROUTES } from "@/utils/constants";
import { NavLink } from "react-router-dom";

interface Link {
  id: number;
  url: string;
  text: string;
}

const links: Link[] = [
  { id: 1, url: ROUTES.HOME, text: "Home" },
  { id: 2, url: ROUTES.FORUM, text: "Forum" },
  { id: 3, url: ROUTES.SERVICE, text: "Services" },
  { id: 4, url: ROUTES.ABOUT, text: "About US" },
];

const NavLinks: React.FC = () => {
  return (
    <>
      {links.map(({ id, url, text }) => {
        return (
          <li key={id}>
            <NavLink
              className={({ isActive }) =>
                `capitalize ${isActive ? "text-primary" : "text-black"}`
              }
              to={url}
            >
              {text}
            </NavLink>
          </li>
        );
      })}
    </>
  );
};

export default NavLinks;
