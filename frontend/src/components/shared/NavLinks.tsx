import { NavLink } from "react-router-dom";
import { useAppSelector } from "@/app/hooks";
import { ROUTES } from "@/utils/constants";
import { SheetClose } from "@/components/ui/sheet";

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

const authenticatedLinks: Link[] = [
  { id: 6, url: ROUTES.DASHBOARD, text: "Dashboard" },
  { id: 7, url: ROUTES.PROFILE, text: "Profile" },
];

const userLinks: Link[] = [
  { id: 5, url: ROUTES.APPOINTMENTS, text: "Appointments" },
];

const NavLinks: React.FC<{ mobile?: boolean }> = ({ mobile = false }) => {
  const user = useAppSelector((state) => state.auth.user);
  const LinkWrapper = mobile ? "div" : "li";

  const renderLink = (url: string, text: string) => {
    if (mobile) {
      return (
        <SheetClose asChild>
          <NavLink
            to={url}
            className={({ isActive }) =>
              `capitalize ${isActive ? "text-blue-500" : "text-primary"}`
            }
          >
            {text}
          </NavLink>
        </SheetClose>
      );
    } else {
      return (
        <NavLink
          to={url}
          className={({ isActive }) =>
            `capitalize ${isActive ? "text-blue-500" : "text-primary"}`
          }
        >
          {text}
        </NavLink>
      );
    }
  };

  const roles = user?.data?.roles || [];
  const isAdmin = roles.includes("ADMIN");
  const isSpecialist = roles.includes("SPECIALIST");
  const isUser = roles.includes("USER") && !isAdmin && !isSpecialist;

  return (
    <>
      {links.map(({ id, url, text }) => (
        <LinkWrapper key={id} className={mobile ? "text-lg" : "relative"}>
          {renderLink(url, text)}
        </LinkWrapper>
      ))}

      {user &&
        isUser &&
        userLinks.map(({ id, url, text }) => (
          <LinkWrapper key={id} className={mobile ? "text-lg" : "relative"}>
            {renderLink(url, text)}
          </LinkWrapper>
        ))}

      {user &&
        authenticatedLinks.map(({ id, url, text }) => (
          <LinkWrapper key={id} className={mobile ? "text-lg" : "relative"}>
            {renderLink(url, text)}
          </LinkWrapper>
        ))}
    </>
  );
};

export default NavLinks;
