import { useEffect } from "react";
import { useAppDispatch, useAppSelector } from "@/app/hooks";
import { fetchMyForums } from "@/features/forumList/forumListThunk";
import { setMyForumsPage } from "@/features/forumList/forumListSlice";
import { ForumListContainer } from "@/components";

const ForumsTab: React.FC = () => {
  const dispatch = useAppDispatch();
  const { myForums, loading, error, myForumsPage, size, myForumsTotalPages } =
    useAppSelector((state) => state.forumList);

  useEffect(() => {
    dispatch(fetchMyForums({ page: myForumsPage, size }));
  }, [dispatch, myForumsPage, size]);

  return (
    <ForumListContainer
      title="My Forums"
      forums={myForums}
      loading={loading}
      error={error}
      page={myForumsPage}
      totalPages={myForumsTotalPages}
      onPageChange={(p) => dispatch(setMyForumsPage(p))}
      onRetry={() => dispatch(fetchMyForums({ page: myForumsPage, size }))}
      emptyMessage="You haven’t created any forums yet."
    />
  );
};

export default ForumsTab;
