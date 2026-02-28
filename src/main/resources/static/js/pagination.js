/**
 * Infinite scroll using IntersectionObserver.
 *
 * @param {object} options
 * @param {string}   options.containerId   - id del elemento al que añadir los items
 * @param {string}   options.sentinelId    - id del centinela que observar
 * @param {string}   options.spinnerId     - id del spinner
 * @param {string}   options.ajaxUrl       - URL base del endpoint de fragmentos
 * @param {boolean}  options.hasMore       - si hay más items tras la primera carga
 * @param {function} [options.getParams]   - función que devuelve un objeto con params extra
 * @param {boolean}  [options.appendToTbody] - si true, añade dentro del <tbody> del container
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
            console.error('[InfiniteScroll] Error cargando página:', err);
        } finally {
            loading = false;
            spinner.style.display = 'none';
        }
    }, { rootMargin: '200px', threshold: 0 });

    observer.observe(sentinel);
}
