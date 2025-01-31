import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Card } from "@/components/ui/card";
import { ChevronDown, Search, MessageCircle, Eye, Check } from "lucide-react";
import { Avatar, AvatarFallback, AvatarImage } from "@/components/ui/avatar";
import forumHeaderImg from "@/assets/images/forumpage/forumheader.png";
import { FaRegHeart, FaRegMessage } from "react-icons/fa6";
import img1 from "@/assets/images/forumpage/img1.png";
import img2 from "@/assets/images/forumpage/img2.png";
import img3 from "@/assets/images/forumpage/img3.png";
import img4 from "@/assets/images/forumpage/img4.png";
import img5 from "@/assets/images/forumpage/img5.png";
import img6 from "@/assets/images/forumpage/img6.png";

const ForumPage = () => {
  const categories = [
    {
      title: "General Discussion",
      src: img1,
    },
    {
      title: "Health & Nutrition",
      src: img2,
    },
    {
      title: "Showcase Your Care",
      src: img3,
    },
    {
      title: "Training & Behavior",
      src: img4,
    },
    {
      title: "Breed-Specific Discussions",
      src: img5,
    },
    {
      title: "Adoption & Rescue",
      src: img6,
    },
  ];

  const featuredTopics = [
    "Mood of the Month",
    "Product Reviews and Recommendations",
    "How do you adapt your care routine for senior pets to keep them happy and healthy?",
  ];

  const recentActivities = [
    {
      user: "Sebastian_Holzki",
      title: "What are the must-have supplies for a new puppy",
      avatar: "S",
      answer:
        "Must-have supplies for a new puppy include a cozy bed, food and water bowls, a high-quality puppy food, a collar and leash, chew toys, and grooming essentials.",
      likes: 12,
      comments: 45,
      time: "2 hours ago",
    },
    {
      user: "Sebastian_Holzki",
      title:
        "Figma Slides 'Version History' doesn't appear to be working. Is there another way to access the Figma Slides version history?",
      avatar: "P",
      answer:
        "In Figma Slides when I go to view my version history and try to select an older version I keep getting the message, 'Something went wrong. Our team is looking into it now. If refreshing the…",
      likes: 8,
      comments: 32,
      time: "4 hours ago",
    },
    {
      user: "Sebastian_Holzki",
      title: "What are some fun indoor activities for dogs?",
      avatar: "M",
      answer:
        "Play interactive games like hide and seek with treats, or enjoy tug-of-war with a sturdy rope toy to keep your dog entertained indoors! These activities are both fun and mentally stimulating.",
      likes: 15,
      comments: 50,
      time: "6 hours ago",
    },
  ];

  const solvedTopics = [
    "Free consultation does my puppy need",
    "How can I socialize my rescue pet with other animals?",
    "What are the best local parks for dog walking?",
    "What are some fun indoor activities for dogs?",
    "What are some engaging toys for cats that keep them engaged?",
    "Natural remedies for itching",
  ];

  return (
    <div className="min-h-screen">
      {/* Header */}
      <section
        className="flex h-96 max-h-[30rem] w-full items-center justify-center pt-16"
        style={{
          backgroundImage: `url('${forumHeaderImg}')`,
          backgroundPosition: "center",
          backgroundSize: "cover",
        }}
      >
        <div className="max-w-6xl px-4">
          <h1 className="mb-4 text-center text-4xl font-bold sm:text-6xl">
            Pet Care Connect
          </h1>
          <p className="tight mb-8 text-center text-xs text-gray-600 md:text-sm">
            Share, Learn, and Connect with Fellow Pet Owners
          </p>

          {/* Search Bar */}
          <div className="max-w-3xl">
            <div className="flex items-center gap-1 rounded-md bg-white p-2">
              <Search className="h-5 w-5 text-gray-400" />
              <Input
                placeholder="Search everything pet care related..."
                className="border-0 bg-transparent px-1 focus-visible:ring-0 focus-visible:ring-offset-0"
              />
            </div>
          </div>
        </div>
      </section>

      {/* Services */}
      <section className="py-16">
        {/* Categories */}
        <div className="mx-auto grid max-w-6xl grid-cols-2 gap-6 px-6 lg:grid-cols-3">
          {categories.map((category, index) => (
            <Card
              key={index}
              className="cursor-pointer border border-0 p-6 shadow transition duration-300 hover:shadow-lg"
            >
              <div className="flex flex-col items-center gap-4">
                <img src={category.src} alt={category.title} />
                <h3 className="text-center font-medium">{category.title}</h3>
              </div>
            </Card>
          ))}
        </div>
      </section>

      {/* Featured Topics */}
      <section className="py-16">
        <div className="section-width">
          <h2 className="mb-6 text-2xl font-semibold">Featured topics</h2>
          <div className="space-y-3">
            {featuredTopics.map((topic, index) => (
              <Card
                key={index}
                className="cursor-pointer border border-0 bg-white p-4"
              >
                <div className="flex items-center justify-between">
                  <div className="flex items-center gap-2">
                    <Avatar className="h-8 w-8 text-gray-600">
                      <AvatarImage src="https://github.com/shadcn.png" />
                      <AvatarFallback>CN</AvatarFallback>
                    </Avatar>
                    <p className="text-sm">{topic}</p>
                  </div>
                  <div className="flex items-center gap-4 text-gray-500">
                    <span className="flex items-center gap-1">
                      <MessageCircle className="h-4 w-4" /> 12
                    </span>
                    <span className="flex items-center gap-1">
                      <Eye className="h-4 w-4" /> 45
                    </span>
                  </div>
                </div>
              </Card>
            ))}
          </div>
        </div>
      </section>

      {/* Two Column Layout */}
      <section className="py-16">
        <div className="section-width grid gap-8 lg:col-span-2 lg:grid-cols-3">
          {/*  */}
          <article className="col-span-2">
            <h2 className="mb-4 text-xl font-semibold">Recent activity</h2>
            <div className="col-span-2 border">
              <div className="space-y-3">
                {recentActivities.map((activity, index) => (
                  <Card key={index} className="border-0 bg-white p-4">
                    <div className="flex items-center gap-4">
                      <div className="flex h-8 w-8 items-center justify-center rounded-full bg-blue-100 text-blue-600">
                        {activity.avatar}
                      </div>
                      <div className="flex-grow">
                        <h3 className="mb-2 text-sm font-semibold">
                          {activity.user}
                        </h3>
                        <p className="text-sm text-gray-900">
                          {activity.title}
                        </p>
                        <div className="mt-2 flex items-center gap-4 text-sm text-xs text-gray-500">
                          <Button
                            variant={"ghost"}
                            className="flex w-auto items-center gap-1 p-0 hover:bg-transparent"
                          >
                            <FaRegHeart className="h-4 w-4" />
                            {activity.likes}
                          </Button>
                          <Button
                            variant={"ghost"}
                            className="flex w-auto items-center gap-1 p-0 hover:bg-transparent"
                          >
                            <FaRegMessage className="h-4 w-4" />
                            {activity.comments}
                          </Button>
                          <span className="flex items-center gap-1">
                            {activity.time}
                          </span>
                        </div>
                      </div>
                    </div>
                  </Card>
                ))}
              </div>
            </div>

            {/* See More Activity Button */}
            <div className="mt-6 text-center">
              <Button
                variant="default"
                className="flex items-center gap-2 text-gray-600 hover:text-gray-800"
                onClick={() => console.log("See more clicked")}
              >
                See more activity
                <ChevronDown className="h-4 w-4" />
              </Button>
            </div>
          </article>
          {/*  */}
          <article className="col-span-1">
            <h2 className="mb-4 text-xl font-semibold">Solved topics</h2>
            <div className="space-y-3">
              {solvedTopics.map((topic, index) => (
                <Card
                  key={index}
                  className="cursor-pointer border-0 bg-white p-4"
                >
                  <div className="flex items-start gap-2">
                    <Check className="mt-1 h-4 w-4 flex-shrink-0 text-green-500" />
                    <p className="text-sm">{topic}</p>
                  </div>
                </Card>
              ))}
            </div>
          </article>
        </div>
      </section>
    </div>
  );
};

export default ForumPage;
