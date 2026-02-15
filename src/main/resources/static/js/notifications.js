$(document).ready(function () {
    const userId = $('#currentUserId').val();
    if (!userId) return;

    // fetch unread count
    fetchUnreadCount();

    // Connect WebSocket for global notifications if not already connected
    // Note: If conversation.html is loaded, it might have its own socket. 
    // Ideally we share one, but separate connections are okay for now given sturcture.
    connectGlobalSocket(userId);
});

function fetchUnreadCount() {
    $.get('/chat/unread-count', function (count) {
        updateMessageBadge(count);
    });
}

function updateMessageBadge(count) {
    const badge = $('#messageCountBadge');
    if (count > 0) {
        badge.text(count).show();
    } else {
        badge.hide();
    }
}

let globalStompClient = null;

function connectGlobalSocket(userId) {
    const socket = new SockJS('/ws');
    globalStompClient = Stomp.over(socket);
    globalStompClient.debug = null; // Disable debug logs to keep console clean

    globalStompClient.connect({}, function (frame) {
        // Subscribe to user-specific message topic
        globalStompClient.subscribe('/topic/messages/' + userId, function (notification) {
            const message = JSON.parse(notification.body);

            // If we are NOT in the active chat with this user, increment badge
            // We can check if we are on the conversation page
            const currentPath = window.location.pathname;
            const isInChat = currentPath.includes('/chat');

            // If we are in chat with this sender, do nothing (chat logic handles it)
            // But actually chat logic marks as read? If not, we might still want to increment?
            // Let's assume complexity: logic should basically always increment unless we read it.
            // For simplicity: Increment badge. If user is in chat, they will read it eventually.

            // Actually, if we are in conversation.html, it receives the same message.
            // We should only show "Notification" count increment if it's a NEW unread message.

            // Optimistic update:
            let count = parseInt($('#messageCountBadge').text()) || 0;
            updateMessageBadge(count + 1);

            // Show toast/notification
            showToast("New message from " + message.senderId.email);
        });
    });
}

function showToast(message) {
    // Simple toast implementation or use Bootstrap Toasts if available
    // For now, let's create a simple fixed element
    const toast = $(`<div class="toast-notification" style="position: fixed; top: 80px; right: 20px; background: #333; color: white; padding: 10px 20px; border-radius: 5px; z-index: 9999; animation: slideIn 0.3s ease;">
        ${message}
    </div>`);

    $('body').append(toast);

    setTimeout(() => {
        toast.fadeOut(500, function () { $(this).remove(); });
    }, 3000);
}
