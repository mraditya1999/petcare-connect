import { ApiResponse } from "./api";

export interface IForum {
  forumId: string;
  title: string;
  content: string;
  tags: string[];
  firstName: string;
  lastName: string;
  email: string;
  likeCount: number;
  commentCount: number;
  likesCount?: number;
  commentsCount?: number;
  createdAt: string;
  updatedAt: string;
  userId: number;
  likedByCurrentUser?: boolean;
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
  updatedAt?: string;
  firstName: string;
  lastName: string;
  email: string;
  parentId: string | null;
  childCount?: number;
  likeCount?: number;
  isEdited?: boolean;
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
  featuredLoading: boolean;
  featuredError: string | null;
  myForumsPage: number;
  myForumsTotalPages: number;
  myForumsTotalElements: number;
  myForumsLoading: boolean;
  myForumsError: string | null;
}

export interface IForumDetailState {
  forum: IForum | null;
  comments: IComment[];
  commentPage: number;
  totalCommentPages: number;
  totalCommentElements: number;
  loading: boolean;
  error: string | null;
  likeProcessing: boolean;
  isLiked: boolean;
}

export interface IForumListPayload {
  forums: IForum[];
  pageNumber: number;
  pageSize: number;
  totalElements: number;
  totalPages: number;
  lastPage: boolean;
}

export type IFetchMyForumsResponse = ApiResponse<IForumListPayload>;
