import React from "react";

interface HeaderProps {
  headerImage: string;
  headerText: string;
}

const Header: React.FC<HeaderProps> = ({ headerImage, headerText }) => {
  return (
    <>
      <header className="relative h-screen overflow-hidden shadow-lg">
        <img
          src={headerImage}
          alt="Person holding a dog"
          className="absolute inset-0 h-full w-full object-cover"
        />
        <div className="absolute inset-0 h-full w-full bg-black opacity-50"></div>
        <div className="absolute bottom-24 left-6 z-10 flex max-w-[50rem] justify-center p-4 sm:bottom-24 sm:left-20">
          <h2 className="text-3xl text-white sm:text-5xl md:text-7xl">
            {headerText}
          </h2>
        </div>
      </header>
    </>
  );
};

export default Header;
