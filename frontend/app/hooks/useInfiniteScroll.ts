import { useCallback, useEffect, useRef, useState } from "react";

export interface PageData<T> {
  content: T[];
  last?: boolean;
  hasNext?: boolean;
}

export function useInfiniteScroll<T>(
  fetchData: (page: number) => Promise<PageData<T>>,
  initialData?: PageData<T>,
  resetDependencies: unknown[] = [],
) {
  const [items, setItems] = useState<T[]>(initialData?.content ?? []);
  const [page, setPage] = useState(0);
  const [loading, setLoading] = useState(false);

  const loadingRef = useRef(false);
  const hasMoreRef = useRef(false);
  const observerTarget = useRef<HTMLDivElement | null>(null);

  const getHasMore = (data?: PageData<T>) => {
    if (!data) return false;
    if (data.hasNext !== undefined) return data.hasNext;
    if (data.last !== undefined) return !data.last;
    return false;
  };

  const [hasMore, setHasMoreState] = useState(getHasMore(initialData));

  const setHasMore = (value: boolean) => {
    hasMoreRef.current = value;
    setHasMoreState(value);
  };

  const loadMore = useCallback(async () => {
    if (loadingRef.current || !hasMoreRef.current) return;

    loadingRef.current = true;
    setLoading(true);

    try {
      const nextPage = page + 1;
      const data = await fetchData(nextPage);

      setItems((current) => [...current, ...(data.content ?? [])]);
      setPage(nextPage);
      setHasMore(getHasMore(data));
    } finally {
      loadingRef.current = false;
      setLoading(false);
    }
  }, [fetchData, page]);

  useEffect(() => {
    setItems(initialData?.content ?? []);
    setPage(0);
    setHasMore(getHasMore(initialData));
  }, resetDependencies);

  useEffect(() => {
    hasMoreRef.current = hasMore;
  }, [hasMore]);

  useEffect(() => {
    const handleScroll = () => {
      const scrollPosition = window.innerHeight + window.scrollY;
      const pageHeight = document.documentElement.scrollHeight;

      if (scrollPosition >= pageHeight - 300) {
        loadMore();
      }
    };

    window.addEventListener("scroll", handleScroll);

    // Por si la página no tiene altura suficiente al inicio
    handleScroll();

    return () => {
      window.removeEventListener("scroll", handleScroll);
    };
  }, [loadMore]);

  return {
    items,
    setItems,
    loading,
    hasMore,
    loadMore,
    observerTarget,
  };
}