$('#query-button').click(function (e) {
    e.preventDefault()
    let query = $('#query-input').val()
    window.location = "http://localhost:8080/index?query="+query
})

$('#query-form').submit(function (e) {
    e.preventDefault()
    let query = $('#query-input').val()
    window.location = "http://localhost:8080/index?query="+query
})

$(document).ready(function () {
    let query = getURLParameter("query")
    if(query !== undefined && query != null && query !== '') {
        var repoTable = $('#repository-table').DataTable({
            "ajax": {
                "url": "/analytics/search?query=" + query,
                "dataSrc": "result"
            },
            "columns" : [
                {"data": "name"},
                {"data": "owner"}
            ]
        });
        $('#repository-table tbody').on('click', 'tr', function (){
            var data = repoTable.row( this ).data()
            let query = getURLParameter("query")
            window.location = "http://localhost:8080/index?query="+query+"&name="+data.name+"&owner="+data.owner
        });
    }
})

$(document).ready(function () {
    let name = getURLParameter("name")
    let owner = getURLParameter("owner")
    if(name !== undefined && name != null && name !== '' &&
        owner !== undefined && owner != null && owner !== '') {
        $.ajax({
            url: "/analytics/"+owner+"/"+name+"/commits",
            success: function (response) {
                renderNoOfCommitChart(response)
                renderCommittersTable(response)
                renderCommitTimelineTable(response)
            }
        })
    }
})

function renderCommittersTable(response) {
    let table = $('#committer-table').DataTable();
    var list = []
    $.each(JSON.parse(JSON.stringify(response.result["committers"])), function (index, value) {
        console.info("Data: " + value)
        list.push(new Array(value));
    });
    table.rows.add(list).draw()
}

function renderCommitTimelineTable(response) {
    let table = $('#commit-timeline-table').DataTable({
        data: response.result["commit_data"]["commit_timelines"],
        "columns" : [
            {"data": "timestamp"},
            {"data": "sha"},
            {"data": "message"},
        ]
    });
}

function renderNoOfCommitChart(response) {
    var names = Object.keys(response.result["commit_data"]["committers_impact"])
    var counts = Object.values(response.result["commit_data"]["committers_impact"])
    var ctx = document.getElementById("noOfCommits");
    var myLineChart = new Chart(ctx, {
        type: 'horizontalBar',
        data: {
            labels: names,
            datasets: [{
                label: "No of Commits",
                backgroundColor: "rgba(2,117,216,1)",
                borderColor: "rgba(2,117,216,1)",
                data: counts
            }],
        },
        options: {
            scales: {
                xAxes: [{
                    gridLines: {
                        display: true
                    },
                    ticks: {
                        min: 0,
                        max: Math.max(names.length)
                    }
                }],
                yAxes: [{
                    ticks: {
                        min: 0,
                        max: Math.max(counts)
                    },
                    gridLines: {
                        display: true
                    }
                }],
            },
            legend: {
                display: true
            }
        }
    });
}

function getURLParameter(sParam) {
    const sPageURL = window.location.search.substring(1);
    const sURLVariables = sPageURL.split('&');
    for (let i = 0; i < sURLVariables.length; i++) {
        let sParameterName = sURLVariables[i].split('=');
        if (sParameterName[0] === sParam) {
            return sParameterName[1];

        }
    }
}
