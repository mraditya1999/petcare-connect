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
}

export interface IPaginationInfo {
  size: number;
  number: number;
  totalElements: number;
  totalPages: number;
}

export interface IComment {
  commentId: string;
  forumId: string;
  userId: number;
  text: string;
  createdAt: string;
  firstName: string;
  lastName: string;
  email: string;
  parentId: string | null;
  likedByUsers: number[];
  replies: IComment[];
}

export interface IForumListState {
  forums: IForum[];
  featuredForums: IForum[];
  myForums: IForum[];
  page: number;
  size: number;
  totalPages: number;
  totalElements: number;
  sortBy: string;
  sortDir: "asc" | "desc";
  searchTerm: string;
  tagSearchTerm: string;
  loading: boolean;
  error: string | null;
  myForumsPage: number;
  myForumsTotalPages: number;
  myForumsTotalElements: number;
}

export interface IForumDetailState {
  forum: IForum | null;
  comments: IComment[];
  commentPage: number;
  totalCommentPages: number;
  loading: boolean;
  error: string | null;
  likeProcessing: boolean;
  isLiked: boolean;
}

export interface IFetchMyForumsResponse {
  message: string;
  data: {
    content: IForum[];
    page: {
      size: number;
      number: number;
      totalElements: number;
      totalPages: number;
    };
  };
}
