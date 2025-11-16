// import { Check } from "lucide-react";
// import { Card } from "../ui/card";

// const solvedTopics = [
//   "Free consultation does my puppy need",
//   "How can I socialize my rescue pet with other animals?",
//   "What are the best local parks for dog walking?",
//   "What are some fun indoor activities for dogs?",
//   "What are some engaging toys for cats that keep them engaged?",
//   "Natural remedies for itching",
// ];
// const SolvedTopics = () => {
//   return (
//     <div className="space-y-3">
//       {solvedTopics.map((topic, index) => (
//         <Card key={index} className="cursor-pointer border-0 bg-white p-4">
//           <div className="flex items-start gap-2">
//             <Check className="mt-1 h-4 w-4 flex-shrink-0 text-green-500" />
//             <p className="text-sm">{topic}</p>
//           </div>
//         </Card>
//       ))}
//     </div>
//   );
// };

// export default SolvedTopics;
import {
  Accordion,
  AccordionItem,
  AccordionTrigger,
  AccordionContent,
} from "@/components/ui/accordion";
import { Check } from "lucide-react";

const solvedTopics = [
  {
    title: "Free consultation does my puppy need",
    description:
      "Puppies usually need a general check-up, vaccinations, and guidance on nutrition and early training. A vet can also advise you on growth patterns and preventive care.",
  },
  {
    title: "How can I socialize my rescue pet with other animals?",
    description:
      "Socializing a rescue pet requires patience and slow introductions. Start with controlled exposure, reward calm behavior, and avoid overwhelming environments until your pet feels secure.",
  },
  {
    title: "What are the best local parks for dog walking?",
    description:
      "Look for parks that provide open spaces, shade, and clean walking paths. Some parks also include designated off-leash zones where dogs can safely play.",
  },
  {
    title: "What are some fun indoor activities for dogs?",
    description:
      "Indoor activities like puzzle feeders, hide-and-seek, scent games, and tug toys help keep your dog mentally and physically stimulated even when you're indoors.",
  },
  {
    title: "What are some engaging toys for cats that keep them engaged?",
    description:
      "Cats enjoy interactive toys like feather wands, laser pointers, puzzle feeders, and small balls. Rotating toys weekly also helps prevent boredom.",
  },
  {
    title: "Natural remedies for itching",
    description:
      "Oatmeal baths, coconut oil, and hypoallergenic diets can help reduce itching. However, persistent scratching may indicate allergies or skin conditions that require a vet check.",
  },
];

const SolvedTopics = () => {
  return (
    <Accordion type="single" collapsible className="space-y-3">
      {solvedTopics.map((topic, index) => (
        <AccordionItem
          key={index}
          value={`item-${index}`}
          className="rounded-lg border bg-white px-4"
        >
          <AccordionTrigger className="flex items-start gap-2 py-3">
            <Check className="mt-1 h-4 w-4 text-green-500" />
            <span className="text-left text-sm">{topic.title}</span>
          </AccordionTrigger>

          <AccordionContent>
            <p className="pb-3 text-sm text-gray-600">{topic.description}</p>
          </AccordionContent>
        </AccordionItem>
      ))}
    </Accordion>
  );
};

export default SolvedTopics;
