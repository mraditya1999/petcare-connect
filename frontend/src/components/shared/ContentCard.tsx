import React from "react";
import { ROUTES } from "@/utils/constants";
import { Link } from "react-router-dom";

interface ContentCardProps {
  image: string;
  imageClass?: string;
  heading: string;
  text: string;
  isOdd: boolean;
  icon?: React.ReactNode;
  buttonText?: string;
}

const ContentCard: React.FC<ContentCardProps> = ({
  image,
  text,
  heading,
  isOdd,
  imageClass,
  icon,
  buttonText,
}) => {
  return (
    <div
      className={`flex ${isOdd ? "md:flex-row-reverse" : "md:flex-row"} section-width flex-col items-center justify-between md:flex-row`}
    >
      <div className="flex h-96 flex-1 justify-center overflow-hidden rounded-lg p-4">
        <img
          src={image}
          alt="Content"
          className={`block object-cover shadow-lg md:h-auto ${imageClass}`}
        />
      </div>
      <div className="flex flex-1 flex-col items-center justify-center p-4">
        <div className="mb-6">
          <h2 className="mb-4 text-3xl font-medium text-gray-800 md:text-4xl">
            {heading}
          </h2>
          <p className="text-gray-400">{text}</p>
        </div>
        <Link
          to={ROUTES.ABOUT}
          className="text-md flex items-center gap-3 self-start rounded-full bg-primary px-4 py-2 py-3 text-sm text-white md:px-6 md:py-4"
        >
          {buttonText} {icon && <span className="mr-2">{icon}</span>}
        </Link>
      </div>
    </div>
  );
};

export default ContentCard;
