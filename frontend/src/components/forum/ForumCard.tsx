import { Link } from "react-router-dom";
import { IForum } from "@/types/forum-types";
import { Card } from "@/components/ui/card";
import { Avatar, AvatarFallback, AvatarImage } from "@/components/ui/avatar";
import { Button } from "../ui/button";
import { FaRegHeart, FaRegMessage } from "react-icons/fa6";
import { ROUTES } from "@/utils/constants";
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
          <Card className="border bg-white p-4 shadow-sm transition hover:shadow-md dark:border-gray-700 dark:bg-gray-800 dark:hover:shadow-gray-900">
            <div className="flex items-center gap-4">
              <Avatar className="h-10 w-10">
                <AvatarImage
                  src={forum?.firstName || ""}
                  alt={forum.firstName}
                />
                <AvatarFallback>
                  {forum.firstName?.charAt(0) || "?"}
                </AvatarFallback>
              </Avatar>

              <div className="flex-grow">
                <h3 className="mb-1 text-sm font-semibold capitalize text-gray-900 dark:text-gray-100">
                  {forum.firstName} {forum.lastName}
                </h3>

                <p className="text-sm capitalize text-gray-800 dark:text-gray-200">
                  {forum.title}
                </p>

                <div
                  className="prose dark:prose-invert mt-1 max-w-none text-xs text-gray-600 dark:text-gray-300"
                  dangerouslySetInnerHTML={{
                    __html: truncateContent(forum.content),
                  }}
                />

                <div className="mt-2 flex items-center gap-4 text-xs text-gray-500 dark:text-gray-400">
                  <Button
                    variant="ghost"
                    className="flex w-auto items-center gap-1 p-0 hover:bg-transparent dark:hover:text-gray-200"
                  >
                    <FaRegHeart className="h-4 w-4" />
                    {forum.likesCount || 0}
                  </Button>

                  <Button
                    variant="ghost"
                    className="flex w-auto items-center gap-1 p-0 hover:bg-transparent dark:hover:text-gray-200"
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
