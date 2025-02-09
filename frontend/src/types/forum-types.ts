export interface IComment {
  userId: string;
  text: string;
}

export interface IForum {
  forumId: string;
  userId: string;
  firstName: string;
  lastName: string;
  email: string;
  title: string;
  content: string;
  createdAt: string | null;
  updatedAt: string | null;
  likes: { userId: string }[];
  comments: IComment[] | null;
  tags: string[];
  userProfile?: string | null; 
}
