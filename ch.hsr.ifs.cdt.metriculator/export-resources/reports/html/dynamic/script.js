;

require(['jquery.tablesorter', 'jquery.sticky-table-header'], function(){

    var $ = jQuery;

    $(function () {

        $("#tree table").tablesorter();
        $("#tree table").stickyTableHeaders({fixedOffset: 10});

        $("#tree tr").click(function(){
            $(this).toggleClass("selected");
        })
    });
});
