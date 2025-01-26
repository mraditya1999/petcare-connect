import React from "react";

interface ContentCardProps {
  image: string;
  imageClass?: string;
  heading: string;
  text: string;
  isOdd: boolean;
}

const ContentCard: React.FC<ContentCardProps> = ({
  image,
  text,
  heading,
  isOdd,
  imageClass,
}) => {
  return (
    <div
      className={`flex ${isOdd ? "flex-row-reverse" : "flex-row"} section-width items-center justify-between`}
    >
      <div className="flex w-1/2 justify-center overflow-hidden rounded-lg p-4">
        <img
          src={image}
          alt="Content"
          className={`h-auto w-full object-cover shadow-lg ${imageClass}`}
        />
      </div>
      <div className="flex w-1/2 justify-center p-4">
        <div className="w-5/6">
          <h2 className="mb-4 text-4xl font-medium text-gray-800">{heading}</h2>
          <p className="text-md text-gray-600">{text}</p>
        </div>
      </div>
    </div>
  );
};

export default ContentCard;
