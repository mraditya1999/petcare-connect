import React from 'react';
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Card } from "@/components/ui/card";
import { ChevronDown, Search, MessageCircle, Eye, Check } from "lucide-react";
import forumHeaderImg from "@/assets/images/forumpage/forumheader.png";

const ForumPage = () => {
  const categories = [
    {
      title: "General Discussion",
      icon: "🌿",
      color: "bg-emerald-100",
    },
    {
      title: "Health & Nutrition",
      icon: "➕",
      color: "bg-blue-100",
    },
    {
      title: "Showcase Your Care",
      icon: "👥",
      color: "bg-sky-100",
    },
    {
      title: "Training & Behavior",
      icon: "👤",
      color: "bg-gray-100",
    },
    {
      title: "Breed-Specific Discussions",
      icon: "😊",
      color: "bg-purple-100",
    },
    {
      title: "Adoption & Rescue",
      icon: "🎯",
      color: "bg-red-100",
    },
  ];

  const featuredTopics = [
    "Mood of the Month 🌿",
    "Product Reviews and Recommendations 🛍️",
    "How do you adapt your care routine for senior pets to keep them happy and healthy? 🐾",
  ];

  const recentActivities = [
    {
      user: "Sebastian",
      avatar: "S",
      question: "What are the must-have supplies for a new puppy? 🐶",
      replies: 12,
      views: 45,
      time: "2 hours ago"
    },
    {
      user: "Patricia",
      avatar: "P",
      question: "Best indoor activities for dogs during winter?",
      replies: 8,
      views: 32,
      time: "4 hours ago"
    },
    {
      user: "Michael",
      avatar: "M",
      question: "Recommended pet insurance providers?",
      replies: 15,
      views: 50,
      time: "6 hours ago"
    }
  ];

  const solvedTopics = [
    "Free consultation does my puppy need",
    "How can I socialize my rescue pet with other animals?",
    "What are the best local parks for dog walking?",
    "What are some fun indoor activities for dogs?",
    "What are some engaging toys for cats that keep them engaged?",
    "Natural remedies for itching"
  ];

  return (
    <div className="min-h-screen">
      {/* Header */}
      <div className="w-full h-[30rem] flex items-center justify-center text-center" style={{ backgroundImage: `url('${forumHeaderImg}')`,backgroundPosition:'center', backgroundSize:'cover' }}
      >
        <div className="max-w-6xl mx-auto px-4">
          <h1 className="text-5xl font-bold mb-2">Pet Care Connect</h1>
          <p className="text mb-8">Share, Learn, and Connect with Fellow Pet Owners</p>
          
          {/* Search Bar */}
          <div className="max-w-2xl mx-auto relative">
            <Input 
              placeholder="Search everything pet care related..." 
              className="pl-10 py-3 rounded-full shadow-sm bg-white border-0"
            />
            <Search className="absolute left-3 top-1/2 -translate-y-1/2 text-gray-400 h-5 w-5" />
          </div>
        </div>
      </div>

{/*  */}
      <div className="max-w-6xl mx-auto px-4 py-8">
        {/* Categories */}
        <div className="grid grid-cols-2 lg:grid-cols-3 gap-6 mb-12">
          {categories.map((category, index) => (
            <Card 
              key={index}
              className="p-6 hover:shadow-lg transition-shadow cursor-pointer border-0 bg-white"
            >
              <div className="flex items-center gap-4">
                <span className={`text-2xl ${category.color} p-3 rounded-full`}>
                  {category.icon}
                </span>
                <h3 className="font-medium">{category.title}</h3>
              </div>
            </Card>
          ))}
        </div>

        {/* Two Column Layout */}
        <div className="grid lg:grid-cols-3 gap-8">
          {/* Left Column - Featured & Recent */}
          <div className="lg:col-span-2 space-y-8">
            {/* Featured Topics */}
            <section>
              <h2 className="text-xl font-semibold mb-4">Featured topics</h2>
              <div className="space-y-3">
                {featuredTopics.map((topic, index) => (
                  <Card 
                    key={index}
                    className="p-4 hover:shadow-md transition-shadow cursor-pointer bg-white border-0"
                  >
                    <div className="flex items-center justify-between">
                      <p>{topic}</p>
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
            </section>

            {/* Recent Activity */}
            <section>
              <h2 className="text-xl font-semibold mb-4">Recent activity</h2>
              <div className="space-y-3">
                {recentActivities.map((activity, index) => (
                  <Card key={index} className="p-4 bg-white border-0">
                    <div className="flex items-center gap-4">
                      <div className="w-10 h-10 rounded-full bg-blue-100 text-blue-600 flex items-center justify-center">
                        {activity.avatar}
                      </div>
                      <div className="flex-grow">
                        <h3 className="font-medium">{activity.user}</h3>
                        <p className="text-gray-600 text-sm">{activity.question}</p>
                        <div className="flex items-center gap-4 mt-2 text-sm text-gray-500">
                          <span className="flex items-center gap-1">
                            <MessageCircle className="h-4 w-4" /> {activity.replies}
                          </span>
                          <span className="flex items-center gap-1">
                            <Eye className="h-4 w-4" /> {activity.views}
                          </span>
                          <span>{activity.time}</span>
                        </div>
                      </div>
                    </div>
                  </Card>
                ))}
              </div>

              {/* See More Activity Button */}
              <div className="text-center mt-6">
                <Button
                  variant="ghost"
                  className="text-gray-600 hover:text-gray-800 flex items-center gap-2"
                  onClick={() => console.log('See more clicked')}
                >
                  See more activity
                  <ChevronDown className="h-4 w-4" />
                </Button>
              </div>
            </section>
          </div>

          {/* Right Column - Solved Topics */}
          <div>
            <h2 className="text-xl font-semibold mb-4">Solved topics</h2>
            <div className="space-y-3">
              {solvedTopics.map((topic, index) => (
                <Card 
                  key={index}
                  className="p-4 hover:shadow-md transition-shadow cursor-pointer bg-white border-0"
                >
                  <div className="flex items-start gap-2">
                    <Check className="h-4 w-4 text-green-500 mt-1 flex-shrink-0" />
                    <p className="text-sm">{topic}</p>
                  </div>
                </Card>
              ))}
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default ForumPage;