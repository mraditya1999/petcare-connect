import img1 from "@/assets/images/forumpage/img1.png";
import img2 from "@/assets/images/forumpage/img2.png";
import img3 from "@/assets/images/forumpage/img3.png";
import img4 from "@/assets/images/forumpage/img4.png";
import img5 from "@/assets/images/forumpage/img5.png";
import img6 from "@/assets/images/forumpage/img6.png";
import { Card } from "../ui/card";

const categories = [
  { title: "General Discussion", src: img1 },
  { title: "Health & Nutrition", src: img2 },
  { title: "Showcase Your Care", src: img3 },
  { title: "Training & Behavior", src: img4 },
  { title: "Breed-Specific Discussions", src: img5 },
  { title: "Adoption & Rescue", src: img6 },
];

const Categories = () => {
  return (
    <div className="section-width mx-auto grid max-w-6xl grid-cols-2 gap-3 px-6 md:gap-6 lg:grid-cols-3">
      {categories.map((category, index) => (
        <Card
          key={index}
          className="cursor-pointer border border-gray-200 bg-white p-6 shadow transition duration-300 hover:shadow-lg dark:border-gray-700 dark:bg-gray-800 dark:hover:shadow-gray-700/30"
        >
          <div className="flex flex-col items-center gap-4">
            <img src={category.src} alt={category.title} />
            <h3 className="md:text-md text-center text-sm font-medium text-gray-800 dark:text-gray-200">
              {category.title}
            </h3>
          </div>
        </Card>
      ))}
    </div>
  );
};

export default Categories;
