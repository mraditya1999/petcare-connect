// import { ROUTES } from "@/utils/constants";
import { NavLink } from "react-router-dom";
import { useAppSelector } from "@/app/hooks";
import { ROUTES } from "@/utils/constants";

interface Link {
  id: number;
  url: string;
  text: string;
}

const links: Link[] = [
  { id: 1, url: ROUTES.HOME, text: "Home" },
  { id: 4, url: ROUTES.ABOUT, text: "About" },
  { id: 2, url: ROUTES.FORUM, text: "Forum" },
  { id: 3, url: ROUTES.SERVICE, text: "Services" },
];

const NavLinks: React.FC = () => {
  const user = useAppSelector((state) => state.auth.user);

  return (
    <>
      {links.map(({ id, url, text }) => (
        <li key={id} className="relative">
          <NavLink
            className={({ isActive }) =>
              `capitalize ${isActive ? "text-blue-500" : "text-primary"}`
            }
            to={url}
          >
            {text}
          </NavLink>
        </li>
      ))}
      {user && (
        <li key="profile" className="relative">
          <NavLink
            className={({ isActive }) =>
              `capitalize ${isActive ? "text-blue-500" : "text-primary"}`
            }
            to={ROUTES.PROFILE}
          >
            Profile
          </NavLink>
        </li>
      )}
    </>
  );
};

export default NavLinks;
