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
        <div className="absolute inset-0 h-full bg-black opacity-50"></div>
        <div className="absolute bottom-24 left-20 z-10 flex w-[50rem] justify-center p-4">
          <h2 className="text-center text-7xl text-white">{headerText}</h2>
        </div>
      </header>
    </>
  );
};

export default Header;
