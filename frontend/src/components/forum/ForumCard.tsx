import { Card } from "../ui/card";
import { ROUTES } from "@/utils/constants";
import { Link } from "react-router-dom";
import { IForum } from "@/types/forum-types";
import { Avatar, AvatarFallback, AvatarImage } from "@/components/ui/avatar";
import { Button } from "../ui/button";
import { FaRegHeart, FaRegMessage } from "react-icons/fa6";
import { formatRelativeTime, truncateContent } from "@/utils/helpers";

interface ForumCardProps {
  forums: IForum[];
}

const ForumCard = ({ forums }: ForumCardProps) => {
  return (
    <article className="space-y-3">
      {forums.map((forum: IForum) => (
        <Link
          key={forum.forumId}
          to={`${ROUTES.FORUM}/${forum.forumId}`}
          className="block"
        >
          <Card key={forum.forumId} className="border-2 bg-white p-4 shadow-sm">
            <div className="flex items-center gap-4">
              <div className="flex h-8 w-8 items-center justify-center rounded-full bg-blue-100 text-blue-600">
                <Avatar className="h-8 w-8">
                  <AvatarImage
                    src={forum.userProfile || ""}
                    alt={forum.firstName}
                  />
                  <AvatarFallback>
                    {forum.firstName?.charAt(0) || "?"}
                  </AvatarFallback>
                </Avatar>
              </div>

              <div className="flex-grow">
                <h3 className="mb-2 text-sm font-semibold capitalize">
                  {forum.firstName} {forum.lastName}
                </h3>
                <p className="text-sm capitalize text-gray-900">
                  {forum.title}
                </p>
                <div
                  className="prose mt-1 max-w-none text-xs text-gray-500"
                  dangerouslySetInnerHTML={{
                    __html: truncateContent(forum.content),
                  }}
                />
                <div className="mt-2 flex items-center gap-4 text-xs text-gray-500">
                  <Button
                    variant="ghost"
                    className="flex w-auto items-center gap-1 p-0 hover:bg-transparent"
                  >
                    <FaRegHeart className="h-4 w-4" />
                    {forum.likesCount || 0}
                  </Button>
                  <Button
                    variant="ghost"
                    className="flex w-auto items-center gap-1 p-0 hover:bg-transparent"
                  >
                    <FaRegMessage className="h-4 w-4" />
                    {forum.commentsCount || 0}
                  </Button>
                  <span className="flex items-center gap-1">
                    {formatRelativeTime(forum?.createdAt || "")}
                  </span>
                </div>
              </div>
            </div>
          </Card>
        </Link>
      ))}
    </article>
  );
};

export default ForumCard;
