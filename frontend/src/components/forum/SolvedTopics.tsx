import { Check } from "lucide-react";
import { Card } from "../ui/card";

const solvedTopics = [
  "Free consultation does my puppy need",
  "How can I socialize my rescue pet with other animals?",
  "What are the best local parks for dog walking?",
  "What are some fun indoor activities for dogs?",
  "What are some engaging toys for cats that keep them engaged?",
  "Natural remedies for itching",
];
const SolvedTopics = () => {
  return (
    <div className="space-y-3">
      {solvedTopics.map((topic, index) => (
        <Card key={index} className="cursor-pointer border-0 bg-white p-4">
          <div className="flex items-start gap-2">
            <Check className="mt-1 h-4 w-4 flex-shrink-0 text-green-500" />
            <p className="text-sm">{topic}</p>
          </div>
        </Card>
      ))}
    </div>
  );
};

export default SolvedTopics;
