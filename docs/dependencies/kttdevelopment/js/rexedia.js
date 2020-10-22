const coverRegex  = $("#cover-regex");
const outputRegex = $("#output-regex");
const addRowBtn   = $("#addRow");
const delRowBtn   = $("#deleteRow");

// regex validation //

const invalid = "is-invalid"
const valid   = "is-valid"

$(document).ready(function(){
    coverRegex.on('input', validateRegex);
    outputRegex.on('input', validateRegex);
    addRowBtn.click(addRow);
    delRowBtn.click(deleteRow);

    addRow();

    $(document).on('click', 'button', function(e) {
        e.preventDefault();
    });
})


function validateRegex(e) {
    const regex = e.target.value
    className = e.target.className

    if(!regex){
        className = className.replace(' ' + valid, "");
        className = className.replace(' ' + invalid, "");
    }else if(isValidRegex(regex)){
        className = className.replace(' ' + invalid, "");
        if(!className.includes(' ' + valid))
            className += ' ' + valid;
    }else{
        className = className.replace(' ' + valid, "");
        if(!className.includes(' ' + invalid))
            className += ' ' + invalid;
    }

    e.target.className = className
}

function isValidRegex(string){
    try{
        regex = new RegExp(string);
        return true;
    }catch(e){
        return false;
    }
}

// row handle //

// replace = '#'
const row = `
<div id="#-meta" class="border-bottom mt-2">
    <input type="text" class="form-control form-control-sm mb-2" id="#-meta-name" placeholder="Name">
    <input type="text" class="form-control form-control-sm mb-2" id="#-meta-regex" placeholder="Regex">
    <input type="text" class="form-control form-control-sm mb-2" id="#-meta-replace" placeholder="Replacement String">
</div>
`

var rows = 0;

function addRow(){
    rows++;
    $("#rows").append(row.replaceAll('#', rows));
}

function deleteRow(){
    if(rows <= 1)
        return;
    $("#rows").html($("#rows").html().replaceAll(row.replaceAll('#', rows), ""));
    rows--;
}

// tester //

// import //

// export //