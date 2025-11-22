import { Button } from "../ui/button";
import { Input } from "../ui/input";
import ForumEditor from "./ForumEditor";

const CreateForumSection: React.FC<{
  title: string;
  tags: string[];
  content: string;
  setTitle: (val: string) => void;
  setTags: (val: string[]) => void;
  setContent: (val: string) => void;
  onCreate: () => void;
  loading: boolean;
}> = ({
  title,
  tags,
  content,
  setTitle,
  setTags,
  setContent,
  onCreate,
  loading,
}) => (
  <section className="bg-white py-16 dark:bg-gray-900">
    <div className="section-width mx-auto px-6">
      <h2 className="mb-4 text-xl font-semibold text-gray-900 dark:text-gray-100">
        Create a New Forum
      </h2>
      <Input
        placeholder="Forum Title"
        value={title}
        onChange={(e) => setTitle(e.target.value)}
        className="mb-4 dark:border-gray-700 dark:bg-gray-800 dark:text-gray-100"
      />
      <Input
        placeholder="Tags (comma-separated)"
        value={tags.join(",")}
        onChange={(e) =>
          setTags(e.target.value.split(",").map((t) => t.trim()))
        }
        className="mb-4 dark:border-gray-700 dark:bg-gray-800 dark:text-gray-100"
      />
      <ForumEditor value={content} onChange={setContent} />
      <Button
        onClick={onCreate}
        className="mt-4 bg-primary text-white hover:bg-primary/90 dark:bg-gray-200 dark:text-gray-900 dark:hover:bg-gray-300"
      >
        {loading ? "Creating..." : "Create Forum"}
      </Button>
    </div>
  </section>
);

export default CreateForumSection;