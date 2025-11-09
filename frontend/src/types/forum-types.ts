export interface IComment {
  userId: string;
  text: string;
  createdAt?: string;
}

export interface ILike {
  likeId: string;
  forumId: string;
  userId: string;
  createdAt: string;
}

export interface IForum {
  forumId: string;
  title: string;
  content: string;
  tags: string[];
  firstName: string;
  lastName: string;
  email: string;
  likesCount: number;
  commentsCount: number;
  createdAt: string;
  updatedAt: string;
  userProfile?: string | null;
  comments?: IComment[];
  likes?: ILike[];
}
