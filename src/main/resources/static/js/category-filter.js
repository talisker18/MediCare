$(document).ready(function(){
        $(".category").on("click", function() {
	        name_list = []
	        $("#product-table tr").hide()
	        var flag = 1
	        $("input:checkbox[name=category]:checked").each(function(){
	                flag = 0;
	                var value = $(this).val().toLowerCase();
	                $("#product-table tr").filter(function() {
	                    if($(this).text().toLowerCase().indexOf(value) > -1)
	                        $(this).show()
	                });
	            });
	            if(flag == 1)
	                $("#product-table tr").show()
        });
});