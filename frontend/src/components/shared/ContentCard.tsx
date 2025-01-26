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
      className={`flex ${isOdd ? "flex-row-reverse" : "flex-row"} section-width items-center justify-between`}
    >
      <div className="flex min-h-96 flex-1 justify-center overflow-hidden rounded-lg p-4">
        <img
          src={image}
          alt="Content"
          className={`block h-auto w-3/5 object-cover shadow-lg ${imageClass}`}
        />
      </div>
      <div className="flex flex-1 flex-col items-center p-4">
        <div className="mb-6">
          <h2 className="mb-4 text-4xl font-medium text-gray-800">{heading}</h2>
          <p className="text-gray-400">{text}</p>
        </div>
        <Link
          to={ROUTES.ABOUT}
          className="text-md flex items-center gap-3 self-start rounded-full bg-primary px-6 py-3 py-4 text-white"
        >
          {buttonText} {icon && <span className="mr-2">{icon}</span>}
        </Link>
      </div>
    </div>
  );
};

export default ContentCard;
