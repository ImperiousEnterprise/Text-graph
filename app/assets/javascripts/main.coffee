$(window).on 'load', (e)->
  $("#dropdownMenu2").text("Today").attr("value","today")
  $(".list-group a").first().addClass("active")
  sigparse($(".list-group a").first().attr("value"),$("#dropdownMenu2").attr("value"))

$(".dropdown-menu button").on 'click',(e)->
  $("#dropdownMenu2").text(e.delegateTarget.innerText).attr("value",e.delegateTarget.value)
  sigparse($(".list-group .active").attr("value"),e.delegateTarget.value)

$(".list-group a").on 'click', (e)->
  e.preventDefault()
  $(".list-group .active").removeClass("active")
  $(this).addClass("active")
  $("#dropdownMenu2").text("Today").attr("value","today")
  sigparse($(".list-group .active").attr("value"),$("#dropdownMenu2").attr("value"))

sigparse = (x,y) ->
  $('#sigmaContainer').remove();
  $('#sigmaSpace').html('<div id="sigmaContainer"></div>');
  sigma.parsers.json('/graph?group_id='+x+'&date='+y, {
    container: 'sigmaContainer',
    settings: {
      defaultNodeColor: '#ec5148'
    }
  })

