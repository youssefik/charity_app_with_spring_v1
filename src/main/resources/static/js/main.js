// Initialisation des tooltips Bootstrap
document.addEventListener('DOMContentLoaded', function() {
    var tooltipTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="tooltip"]'));
    var tooltipList = tooltipTriggerList.map(function(tooltipTriggerEl) {
        return new bootstrap.Tooltip(tooltipTriggerEl);
    });

    // Initialisation des popovers Bootstrap
    var popoverTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="popover"]'));
    var popoverList = popoverTriggerList.map(function(popoverTriggerEl) {
        return new bootstrap.Popover(popoverTriggerEl);
    });

    // Validation des formulaires
    var forms = document.querySelectorAll('.needs-validation');
    Array.prototype.slice.call(forms).forEach(function(form) {
        form.addEventListener('submit', function(event) {
            if (!form.checkValidity()) {
                event.preventDefault();
                event.stopPropagation();
            }
            form.classList.add('was-validated');
        }, false);
    });

    // Gestion des alertes
    var alerts = document.querySelectorAll('.alert');
    alerts.forEach(function(alert) {
        setTimeout(function() {
            var bsAlert = new bootstrap.Alert(alert);
            bsAlert.close();
        }, 5000);
    });

    // Gestion des modales
    var modals = document.querySelectorAll('.modal');
    modals.forEach(function(modal) {
        modal.addEventListener('shown.bs.modal', function() {
            var firstInput = modal.querySelector('input, select, textarea');
            if (firstInput) {
                firstInput.focus();
            }
        });
    });

    // Gestion des tableaux de données
    var tables = document.querySelectorAll('.datatable');
    tables.forEach(function(table) {
        if (typeof $.fn.DataTable !== 'undefined') {
            $(table).DataTable({
                language: {
                    url: '//cdn.datatables.net/plug-ins/1.10.24/i18n/French.json'
                }
            });
        }
    });

    // Gestion des graphiques
    var charts = document.querySelectorAll('canvas');
    charts.forEach(function(chart) {
        var ctx = chart.getContext('2d');
        var type = chart.dataset.chartType || 'line';
        var data = JSON.parse(chart.dataset.chartData || '{}');
        var options = JSON.parse(chart.dataset.chartOptions || '{}');

        if (typeof Chart !== 'undefined') {
            new Chart(ctx, {
                type: type,
                data: data,
                options: options
            });
        }
    });
}); 