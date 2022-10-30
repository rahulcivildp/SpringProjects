$(document).ready(function() {
	$("#logoutLink").click(function(e) {
		e.preventDefault();
		document.logoutForm.submit();
	});
	
	customizeDropdownMenu();
});

function customizeDropdownMenu() {
	$(".customDropdown > a").click(function(){
		location.href = this.href;
	});
};

$( function() {
    // run the currently selected effect
    function runEffect() {
      // get effect type from
      var selectedEffect = $( "#effectType" ).val();
 
      // Run the effect
      $( "#topNavBar" ).toggle( selectedEffect, options, 500 );
    };
 
    // Set effect from select menu value
    $( "#toggleButtonDropdown" ).on( "click", function() {
      runEffect();
    });
  } );