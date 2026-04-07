/**
 * Infinite scroll using IntersectionObserver.
 *
 * @param {object} options
 * @param {string}   options.containerId   - id of the element to append items to
 * @param {string}   options.sentinelId    - id of the sentinel element to observe
 * @param {string}   options.spinnerId     - id of the spinner element
 * @param {string}   options.ajaxUrl       - base URL of the fragment endpoint
 * @param {boolean}  options.hasMore       - whether there are more items after the initial load
 * @param {function} [options.getParams]   - function that returns an object with extra params
 * @param {boolean}  [options.appendToTbody] - if true, appends inside the container's <tbody>
 */
function initInfiniteScroll({ containerId, sentinelId, spinnerId, ajaxUrl, hasMore, getParams, appendToTbody }) {
    const sentinel = document.getElementById(sentinelId);
    const spinner  = document.getElementById(spinnerId);

    if (!sentinel || !spinner) return;

    if (!hasMore) {
        sentinel.style.display = 'none';
        return;
    }

    let currentPage = 1;
    let loading     = false;
    let exhausted   = false;

    const observer = new IntersectionObserver(async function(entries) {
        if (!entries[0].isIntersecting || loading || exhausted) return;

        loading = true;
        spinner.style.display = 'flex';

        try {
            const url = new URL(ajaxUrl, window.location.origin);
            url.searchParams.set('page', currentPage);

            const extra = getParams ? getParams() : {};
            Object.entries(extra).forEach(function([k, v]) {
                if (v !== null && v !== undefined && v !== '') {
                    url.searchParams.set(k, v);
                }
            });

            const response = await fetch(url.toString(), {
                headers: { 'X-Requested-With': 'XMLHttpRequest' }
            });

            if (!response.ok) {
                exhausted = true;
                observer.disconnect();
                return;
            }

            const html = await response.text();

            if (!html || html.trim() === '') {
                exhausted = true;
                observer.disconnect();
                sentinel.style.display = 'none';
                return;
            }

            const container = document.getElementById(containerId);
            if (appendToTbody) {
                const tbody = container.querySelector('tbody') || container;
                tbody.insertAdjacentHTML('beforeend', html);
            } else {
                container.insertAdjacentHTML('beforeend', html);
            }

            currentPage++;
        } catch (err) {
            console.error('[InfiniteScroll] Error loading page:', err);
        } finally {
            loading = false;
            spinner.style.display = 'none';
        }
    }, { rootMargin: '200px', threshold: 0 });

    observer.observe(sentinel);
}
