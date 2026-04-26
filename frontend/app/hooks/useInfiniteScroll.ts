import { useState, useEffect, useRef, useCallback } from "react";

export interface PageData<T> {
  content: T[];
  last: boolean;
}

export function useInfiniteScroll<T>(
  fetchData: (page: number) => Promise<PageData<T>>,
  initialData?: PageData<T>,
  resetDependencies: any[] = []
) {
  const [items, setItems] = useState<T[]>(initialData?.content || []);
  const [page, setPage] = useState(0);
  const [hasMore, setHasMore] = useState(!initialData?.last);
  const [loading, setLoading] = useState(false);

  const observerTarget = useRef<HTMLDivElement>(null);

  // Reset the state if dependencies change (e.g. filter or search change)
  useEffect(() => {
    setItems(initialData?.content || []);
    setPage(0);
    setHasMore(!initialData?.last);
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, resetDependencies);

  const loadMore = useCallback(async () => {
    if (loading || !hasMore) return;

    setLoading(true);
    try {
      const nextPageIndex = page + 1;
      const nextPageData = await fetchData(nextPageIndex);

      setItems((prev) => [...prev, ...(nextPageData.content || [])]);
      setPage(nextPageIndex);
      setHasMore(!nextPageData.last);
    } catch (e) {
      console.error("Error loading more items:", e);
    } finally {
      setLoading(false);
    }
  }, [page, hasMore, loading, fetchData]);

  // Observer to trigger loadMore upon intersection
  useEffect(() => {
    const observer = new IntersectionObserver(
      (entries) => {
        if (entries.some((entry) => entry.isIntersecting)) {
          loadMore();
        }
      },
      {
        threshold: 0,
        // Prefetch before the sentinel reaches the viewport bottom.
        rootMargin: "240px 0px",
      }
    );

    if (observerTarget.current) {
      observer.observe(observerTarget.current);
    }

    return () => observer.disconnect();
  }, [loadMore]);

  return { items, loading, hasMore, observerTarget };
}